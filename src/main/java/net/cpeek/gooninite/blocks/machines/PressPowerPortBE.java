package net.cpeek.gooninite.blocks.machines;


import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PressPowerPortBE extends KineticBlockEntity {

    public PressPowerPortBE(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.PRESS_PORT_BE.get(), pos, state);
    }

    @Override
    public void tick(){
        super.tick();

        BlockPos pressPos = worldPosition.below();
        if(!(level.getBlockEntity(pressPos) instanceof MechanicalSinteringPressBE)){
            level.destroyBlock(worldPosition, false);
            return;
        }

        if(level == null || level.isClientSide) return; //serverside only


        BlockEntity press = level.getBlockEntity(pressPos);
        if(press instanceof MechanicalSinteringPressBE){
            float rpm = Math.abs(getSpeed());
            ((MechanicalSinteringPressBE) press).setRPM(rpm);
        }
    }
}
