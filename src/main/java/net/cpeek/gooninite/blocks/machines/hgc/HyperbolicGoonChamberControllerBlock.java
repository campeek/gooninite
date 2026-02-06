package net.cpeek.gooninite.blocks.machines.hgc;


import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.blocks.machines.GooniniteMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import static net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberPartBlock.FORMED;

@SuppressWarnings("deprecation")
public class HyperbolicGoonChamberControllerBlock extends GooniniteMachineBlock {

    public HyperbolicGoonChamberControllerBlock(Properties props) {
        super(props, HyperbolicGoonChamberControllerBE::new);
        this.registerDefaultState(                             // Set a default for the property otherwise game will crash
                this.getStateDefinition().any()
                        .setValue(FORMED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FORMED, false);

    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if(level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof HyperbolicGoonChamberControllerBE be && player instanceof ServerPlayer sp){
            NetworkHooks.openScreen(sp, be, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if(!oldState.is(GooniniteBlocks.HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get())) {
            if (level.getBlockEntity(pos) instanceof HyperbolicGoonChamberControllerBE be) {
                //be.update();
                //be.setMultiblockDirty();
                //be.cleanupMultiblockStates();
            }
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean pMovedByPiston) {
        if(!newState.is(GooniniteBlocks.HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get())) {
            if (level.getBlockEntity(pos) instanceof HyperbolicGoonChamberControllerBE be) {
                if (state.getValue(FORMED)) {
                    //be.breakMultiblock();
                    be.cleanupMultiblockStates();
                }

            }
        }
        super.onRemove(state, level, pos, newState, pMovedByPiston);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {

        System.out.println(direction);
        if(direction != state.getValue(FACING)){
        //if(neighborState.is(GooniniteTags.Blocks.GOON_CHAMBER_BLOCKS)){
            if(level.getBlockEntity(pos) instanceof HyperbolicGoonChamberControllerBE be){
                //be.update();
                be.setMultiblockDirty();
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HyperbolicGoonChamberControllerBE(pPos, pState);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        return !level.isClientSide
                ? createTicker(type, GooniniteBlockEntities.HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get(), HyperbolicGoonChamberControllerBE::serverTick)
                : null;
    }

    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
            BlockEntityType<T> type,
            BlockEntityType<? extends BlockEntity> expectedType,
            BlockEntityTicker<? extends HyperbolicGoonChamberControllerBE> ticker) {
        return type == expectedType? (BlockEntityTicker<T>) ticker : null;
    }
}
