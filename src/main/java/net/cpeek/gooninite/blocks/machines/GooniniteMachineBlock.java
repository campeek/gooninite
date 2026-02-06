package net.cpeek.gooninite.blocks.machines;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class GooniniteMachineBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory;

    public GooniniteMachineBlock(Properties props, BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory) {
        super(props);
        this.registerDefaultState(                             // Set a default for the property otherwise game will crash
                this.getStateDefinition().any()
                        .setValue(FACING, Direction.NORTH)
        );
        this.factory = factory;
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { // override block creation to add state
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {   // set the state correctly when the block is placed
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());  // player faces the block, the front should face the player
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING,                       // let the block rotation be updated
                direction.rotate(state.getValue(FACING))
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return factory.apply(pPos, pState);
    }
}
