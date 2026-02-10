package net.cpeek.gooninite.worldgen;


import com.mojang.serialization.Codec;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.blocks.GooniniteDripBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GoonStalactiteFeature extends Feature<NoneFeatureConfiguration> {
    public GoonStalactiteFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        var level = ctx.level();
        var rand = ctx.random();

        BlockPos origin = ctx.origin();

        int clusterSize = 1+rand.nextInt(4); // max amount of stalactites in cluster

        // try some random spots
        for(int tries = 0; tries < 8; tries++){
            int dX = rand.nextInt(16);
            int dZ = rand.nextInt(16);
            //System.out.println("spawn try " + tries);

            int y = origin.getY() + rand.nextInt(32)-16;
            BlockPos pos = new BlockPos(origin.getX()+dX, y, origin.getZ()+dZ);

            if(!level.isEmptyBlock(pos)) continue; // if there's already something at pos, move on


            for(int i=0; i<=clusterSize; i++) { //  for every cluster we try to spawn
                pos = new BlockPos(pos.getX()-i, pos.getY(), pos.getZ()-i);

                // find ceiling
                final int CEILING_SEARCH_HEIGHT = 20;
                int ceilingHeight = 0;
                for (int c = 0; c <= CEILING_SEARCH_HEIGHT; c++) {
                    BlockPos test = pos.above(c);
                    if (!level.isEmptyBlock(test)) {
                        ceilingHeight=c;
                        break;
                    }
                }
                if(ceilingHeight < 6) {
                    //System.out.println("ceiling too low - height " + ceilingHeight);
                    break; // no ceiling found - can't spawn goon
                }

                BlockPos dripPos = new BlockPos(pos.getX(), y+ceilingHeight-1, pos.getZ());
                int length = 3+rand.nextInt(3);
                placeGoonDrip(level, dripPos, length);
                if(i==clusterSize) {
                    //System.out.println("spawned (?) " + clusterSize + " clusters at " + origin);
                    return true;
                }
            }
        }
        //System.out.println("no valid position at " + origin);
        return false;
    }

    private void placeGoonDrip(LevelAccessor level, BlockPos start, int length){
        for(int i=0; i<length; i++){
            BlockPos p = start.below(i);
            if(!level.isEmptyBlock(p)) break; // if there's a block in the way

            var thiccness = switch(i){
                case 0 -> DripstoneThickness.BASE;
                case 1 -> (length <= 2 ? DripstoneThickness.FRUSTUM : DripstoneThickness.MIDDLE);
                default -> (i == length-1 ? DripstoneThickness.TIP : DripstoneThickness.MIDDLE);
            };

            BlockState state = GooniniteBlocks.GOONINITE_DRIP.get().defaultBlockState()
                    .setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN)
                    .setValue(PointedDripstoneBlock.THICKNESS, thiccness)
                    .setValue(PointedDripstoneBlock.WATERLOGGED, false);

            System.out.println(start);
            level.setBlock(p, state, 2);
        }
    }
}
