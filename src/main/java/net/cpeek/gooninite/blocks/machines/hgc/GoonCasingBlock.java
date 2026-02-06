package net.cpeek.gooninite.blocks.machines.hgc;


import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.blocks.GooniniteTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;


/**
 * GoonCasingBlock is a HyperbolicGoonChamberPartBlock that can react to where it's positioned (change texture etc)
 */
public class GoonCasingBlock extends HyperbolicGoonChamberPartBlock {
    public static final EnumProperty<CasingPart> CASING_PART = EnumProperty.create("casing_part", CasingPart.class);

    public GoonCasingBlock(Properties props) {
        super(props);
        this.registerDefaultState(                             // Set a default for the property otherwise game will crash
                this.getStateDefinition().any()
                        .setValue(CASING_PART, CasingPart.SINGLE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { // override block creation to add state
        super.createBlockStateDefinition(builder);
        builder.add(CASING_PART);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context); // set the state correctly when the block is placed
        return state.setValue(CASING_PART, computeCasingShape(context.getLevel(), context.getClickedPos()));
    }

    /*@Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        state.setValue(CASING_PART, computeCasingShape(level, pos));
    }*/

    private CasingPart computeCasingShape(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockPos below= pos.below();

        //System.out.println("updating block shape");
        CasingPart part = CasingPart.SINGLE;
        if(level.getBlockState(above).is(GooniniteTags.Blocks.GOON_TILEABLE_BLOCKS)){
            part = CasingPart.BOTTOM;
            if(level.getBlockState(below).is(GooniniteTags.Blocks.GOON_TILEABLE_BLOCKS)){
                part = CasingPart.MIDDLE;
            }
        } else if(level.getBlockState(below).is(GooniniteTags.Blocks.GOON_TILEABLE_BLOCKS)){
            part = CasingPart.TOP;
        }

        System.out.println("shape: " + part.getSerializedName());
        return part;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        state = state.setValue(CASING_PART, computeCasingShape((Level) pLevel, pPos));
        return super.updateShape(state, dir, pNeighborState, pLevel, pPos, pNeighborPos);
    }
}
