package net.cpeek.gooninite.blocks.machines.mech_press;


import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
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
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressPowerPortBE be){
        BlockPos pressPos = pos.below();
        BlockEntity pressBE = level.getBlockEntity(pressPos);
        if(pressBE instanceof MechanicalSinteringPressBE press){
            int rpm = (int)Math.abs(be.getSpeed());
            if(press.getRPM() != rpm) {
                // only update if we need to
                System.out.println("Updating RPM");
                System.out.println("Old RPM: " + press.getRPM());
                System.out.println("New RPM: " + rpm);
                press.setRPM(rpm);
            }
        }
    }
}
