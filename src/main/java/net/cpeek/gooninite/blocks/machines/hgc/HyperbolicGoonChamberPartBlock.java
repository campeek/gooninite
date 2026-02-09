package net.cpeek.gooninite.blocks.machines.hgc;


import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.blocks.GooniniteTags;
import net.cpeek.gooninite.particles.GoonParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

/**
 * HyperbolicGoonChamberPartBlock owns fields common across all parts like FACING and FORMED.
 * It also handles non-BE logic for formation/breaking
 */

public class HyperbolicGoonChamberPartBlock extends Block{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    private BlockPos controller;

    public HyperbolicGoonChamberPartBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(                             // Set a default for the property otherwise game will crash
                this.getStateDefinition().any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(FORMED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { // override block creation to add state
        builder.add(FACING);
        builder.add(FORMED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {   // set the state correctly when the block is placed
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(FORMED, false); // player faces the block, the front should face the player
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING,                       // let the block rotation be updated
                direction.rotate(state.getValue(FACING))
        );
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if(!level.isClientSide && state.getBlock() != oldState.getBlock()){
            controller = findController(level, pos, 3);
            if(controller == null) return;
            if(level.getBlockEntity(controller) instanceof HyperbolicGoonChamberControllerBE be){
                be.setMultiblockDirty();
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(level.isClientSide){
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }

        if(state.is(newState.getBlock())){
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }
        //findController(level, pos, 3);
        if(controller!=null) {
            BlockEntity be = level.getBlockEntity(controller);
            if (be instanceof HyperbolicGoonChamberControllerBE chamber) {
                chamber.setMultiblockDirty();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private BlockPos findController(Level level, BlockPos pos, int radius) {

        for(BlockPos p: BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))){
            BlockState state = level.getBlockState(p);
            if(state.is(GooniniteBlocks.HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get())){
                BlockEntity be = level.getBlockEntity(p);
                if(be instanceof HyperbolicGoonChamberControllerBE chamber){
                    //chamber.update();
                    //chamber.setMultiblockDirty();
                    //controller = p;
                    //System.out.println("controller at " + p);
                    return p;
                }
            }
        }

        return null;
        //System.out.println("no controller found");
    }

    public void form(Level level, BlockPos pos, BlockState state){

        float particleVelocity = 0.04f + (level.random.nextFloat()*0.15f);
        Direction dir = state.getValue(FACING);
        Vec3 fwd = Vec3.atLowerCornerOf(dir.getNormal());
        Vec3 vel = fwd.scale(particleVelocity);

        Vec3 spawn = Vec3.atCenterOf(pos).add(fwd.scale(0.55));

        level.addParticle(
                GoonParticles.GOON_JUICE_SQUIRT.get(),
                spawn.x, spawn.y, spawn.z,
                vel.x, vel.y, vel.z
        );
        level.setBlock(pos, state.setValue(FORMED, true), 3);
    }
    public void unform(Level level, BlockPos pos, BlockState state){ // break is reserved in java lol
        controller = null;
        level.setBlock(pos, state.setValue(FORMED, false), 3);
    }
}
