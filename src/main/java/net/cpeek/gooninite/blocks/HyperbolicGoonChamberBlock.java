package net.cpeek.gooninite.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class HyperbolicGoonChamberBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;   // horizontal restricts it to NSEW

    public HyperbolicGoonChamberBlock(Properties props){
        super(props);
        this.registerDefaultState(                             // Set a default for the property otherwise game will crash
                this.getStateDefinition().any()
                        .setValue(FACING, Direction.NORTH)
        );
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

}
