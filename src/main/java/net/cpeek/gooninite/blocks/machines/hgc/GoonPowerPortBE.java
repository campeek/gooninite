package net.cpeek.gooninite.blocks.machines.hgc;


import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.handlers.GoonEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import static net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberPartBlock.FORMED;

public class GoonPowerPortBE extends ChamberPartBlockEntity{

    public final GoonEnergyStorage energyStorage = new GoonEnergyStorage(5000, 5000, 5000){
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) { // anonymous class to fwd received energy to the controller
            if(!canReceive()) return 0;
            if(!getBlockState().getValue(FORMED)) return 0;
            if(controllerPos == null) return 0;
            //if(!formed) return 0;

            //int received = super.receiveEnergy(maxReceive, simulate);
            BlockEntity controller = level.getBlockEntity(controllerPos);
            if(controller instanceof HyperbolicGoonChamberControllerBE be){
                be.setChanged();
                setChanged();
                //System.out.println("sending energy to controller");
                return be.getEnergyStorage().receiveEnergy(maxReceive, simulate);
            }
            return 0;
        }
    };

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energyStorage);

    public GoonPowerPortBE(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.GOON_POWER_PORT_BE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if(cap == ForgeCapabilities.ENERGY && side == getBlockState().getValue(HyperbolicGoonChamberPartBlock.FACING)){
            return energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void cleanup() {
        energyCap.invalidate();
    }
}
