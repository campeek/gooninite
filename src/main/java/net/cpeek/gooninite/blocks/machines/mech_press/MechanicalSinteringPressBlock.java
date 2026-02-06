package net.cpeek.gooninite.blocks.machines.mech_press;


import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class MechanicalSinteringPressBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;


    private static final VoxelShape SHAPE = Block.box(  // 1x2x1 collision shape
            0, 0, 0,
            16, 32, 16);


    public MechanicalSinteringPressBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
                this.getStateDefinition().any().setValue(FACING, Direction.NORTH)
        );
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        //return level.isClientSide ? null : createTickerHelper(type, GooniniteBlockEntities.PRESS_BE.get(), MechanicalSinteringPressBE::serverTick);
        return createTickerHelper(type, GooniniteBlockEntities.PRESS_BE.get(),
                level.isClientSide ? MechanicalSinteringPressBE::clientTick : MechanicalSinteringPressBE::serverTick);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        if(level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof MechanicalSinteringPressBE pressBE &&
                player instanceof ServerPlayer sp){
            NetworkHooks.openScreen(sp, pressBE, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if(!level.isClientSide()){
            BlockPos port = pos.above();
            if(neighborPos.equals(port)){
                if(!level.getBlockState(port).is(GooniniteBlocks.PRESS_PORT.get())){
                    level.setBlock(port,
                            GooniniteBlocks.PRESS_PORT.get().defaultBlockState()
                                    .setValue(PressPowerPortBlock.FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)),
                            3);
                }
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(level.isClientSide) return; // server only

        BlockPos above = pos.above();

        if(!level.getBlockState(above).canBeReplaced()){ // if the block above isn't open
            level.destroyBlock(pos, true); // commit die
            return;
        } else {
            // add our dummy port block
            level.setBlock(above, GooniniteBlocks.PRESS_PORT.get().defaultBlockState().setValue(
                    BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)),
                    3);

        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        super.onRemove(state, level, pos, newState, moving);
        if(!level.isClientSide && state.getBlock() != newState.getBlock()){
            BlockPos port = pos.above();
            if(level.getBlockState(port).is(GooniniteBlocks.PRESS_PORT.get())){
                level.destroyBlock(port, false);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {

        BlockPos port = pos.above();
        Direction newFacing = direction.rotate(state.getValue(FACING));

        BlockState portState = level.getBlockState(port);
        level.setBlock(port, portState.setValue(FACING, newFacing), 3);

        return state.setValue(FACING, newFacing);

    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        if(pos.getY() >= level.getMaxBuildHeight() - 1) return null;
        if(!level.getBlockState(pos.above()).canBeReplaced(context)) return null;

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new MechanicalSinteringPressBE(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
