package net.cpeek.gooninite.blocks;

import net.cpeek.gooninite.particles.GoonParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GooniniteDripBlock extends PointedDripstoneBlock {
    public GooniniteDripBlock(Properties p) {
        super(p);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(!level.isClientSide) return; // don't run on server = drips are clientside only
        if(!canDrip(blockState)) return; // if we're not supposed to drip, dont

        if(randomSource.nextFloat() < 0.15f){ // drip frequency
            double x = blockPos.getX() + 0.5f;
            double y = blockPos.getY() + 0.05f;
            double z = blockPos.getZ() + 0.5f;

            level.addParticle(
                    GoonParticles.GOON_JUICE_DRIPPING.get(),
                    x, y, z,
                    0, 0, 0
            );
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        //return super.getStateForPlacement(blockPlaceContext);
        LevelAccessor level = blockPlaceContext.getLevel();
        BlockPos clickedPos = blockPlaceContext.getClickedPos();

        Direction oppositeLooking = blockPlaceContext.getNearestLookingVerticalDirection().getOpposite();

        Direction preferredTipDir = calculateTipDirection(level, clickedPos, oppositeLooking);
        if(preferredTipDir == null){
            return null;
        } else {
            boolean allowedToMerge = !blockPlaceContext.isSecondaryUseActive(); // <- player crouching
            DripstoneThickness thickness = calculateGoonDripThickness(level, clickedPos, preferredTipDir, allowedToMerge);
            return thickness == null ? null : this.defaultBlockState().setValue(TIP_DIRECTION, preferredTipDir)
                    .setValue(THICKNESS, thickness)
                    .setValue(WATERLOGGED, level.getFluidState(clickedPos).getType() == Fluids.WATER);
        }

    }

    @Nullable
    private static Direction calculateTipDirection(LevelReader level, BlockPos pos, Direction preferredDir){
        Direction tipDir;
        if(isValidGoonDripPlacement(level, pos, preferredDir)){
            tipDir = preferredDir;
        } else {
            if(!isValidGoonDripPlacement(level, pos, preferredDir.getOpposite())){
                return null;
            }
            tipDir = preferredDir.getOpposite();
        }

        return tipDir;
    }

    private static boolean isValidGoonDripPlacement(LevelReader level, BlockPos pos, Direction dir){
        BlockPos behind = pos.relative(dir.getOpposite());
        BlockState behindState = level.getBlockState(behind);
        return behindState.isFaceSturdy(level, behind, dir) || isGoonMaterialWithDirection(behindState, dir);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return isValidGoonDripPlacement(levelReader, blockPos, blockState.getValue(TIP_DIRECTION));
    }

    @Override
    @NotNull
    public BlockState updateShape(BlockState blockState, Direction changedSide, BlockState neighbor, LevelAccessor levelAccessor, BlockPos myPos, BlockPos neighborPos) {

        // tick water if we're wet uwu
        if(blockState.getValue(WATERLOGGED)){
            levelAccessor.scheduleTick(myPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        // if we didn't change up or down, who cares
        if(changedSide != Direction.UP && changedSide != Direction.DOWN){
            return blockState;
        }

        Direction tipDir = blockState.getValue(TIP_DIRECTION);
        // ignore if we're already scheduled to break
        if(tipDir == Direction.DOWN && levelAccessor.getBlockTicks().hasScheduledTick(myPos, this)){
            return blockState;
        }

        // if our support broke, schedule a tick to break/fall
        if(changedSide == tipDir.getOpposite() && !this.canSurvive(blockState, levelAccessor, myPos)){
            levelAccessor.scheduleTick(myPos, this, (tipDir == Direction.DOWN) ? 2 : 1);
            return blockState;
        }

        boolean wasMerge = blockState.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
        DripstoneThickness newThickness = calculateGoonDripThickness(levelAccessor, myPos, tipDir, wasMerge);
        return blockState.setValue(THICKNESS, newThickness);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
    }

    private static DripstoneThickness calculateGoonDripThickness(LevelReader level, BlockPos pos, Direction tipDir, boolean merged){
        Direction opp = tipDir.getOpposite();

        BlockState ahead = level.getBlockState(pos.relative(tipDir));

        // case 1 - ahead is another goon stick pointing at me; we have to be TIP or TIP_MERGE
        if(isGoonMaterialWithDirection(ahead, opp)){
            return !merged && (ahead.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE) ? DripstoneThickness.TIP : DripstoneThickness.TIP_MERGE;
        }

        // case 2 - ahead is not goon material, there's nothing past me so im the TIP
        else if (!isGoonMaterialWithDirection(ahead, tipDir)) {
            return DripstoneThickness.TIP;
        }

        // case 3 - ahead is also goon material
        else {
            DripstoneThickness thickness = ahead.getValue(THICKNESS);
            if(thickness != DripstoneThickness.TIP && thickness != DripstoneThickness.TIP_MERGE){ // if what's in front isn't TIP
                BlockState behind = level.getBlockState(pos.relative(opp));
                // if what's behind isn't goon material, we're BASE. otherwise, we're MIDDLE
                return !isGoonMaterialWithDirection(behind, tipDir) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
            } else {
                return DripstoneThickness.FRUSTUM; // if TIP is in front, we're FRUSTUM
            }
        }
    }

    private static boolean isGoonMaterialWithDirection(BlockState block, Direction dir){
        return block.is(GooniniteBlocks.GOONINITE_DRIP.get()) && block.getValue(TIP_DIRECTION) == dir;
    }

    public static boolean canDrip(BlockState block){
        DripstoneThickness thickness = block.getValue(THICKNESS);
        Direction dir = block.getValue(TIP_DIRECTION);
        return thickness == DripstoneThickness.TIP && dir == Direction.DOWN;
    }
}
