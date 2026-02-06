package net.cpeek.gooninite.blocks.machines.hgc;


import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.handlers.GoonFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberPartBlock.FORMED;

public class GoonFluidPortBE extends ChamberPartBlockEntity {

    private GoonFluidHandler fluidHandler = new GoonFluidHandler(4000){

        // override fluid handler methods to pass actions to controller

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if(level == null) return 0;
            if(!getBlockState().getValue(FORMED)) return 0;
            if(controllerPos == null) return 0;

            if(level.getBlockEntity(controllerPos) instanceof HyperbolicGoonChamberControllerBE be){
                //be.setChanged();
                setChanged();
                return be.getFluidHandler().fill(resource, action);
            }
            return 0;
        }

    };

    private LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> fluidHandler);

    public GoonFluidPortBE(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.GOON_FLUID_PORT_BE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER && side == getBlockState().getValue(HyperbolicGoonChamberPartBlock.FACING)){
            return fluidCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void cleanup() {
        fluidCap.invalidate();
    }
}
