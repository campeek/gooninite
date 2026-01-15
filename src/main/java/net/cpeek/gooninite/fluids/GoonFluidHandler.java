package net.cpeek.gooninite.fluids;


import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class GoonFluidHandler implements IFluidHandler {

    private final FluidTank fluid;

    public GoonFluidHandler(int capacity){
        fluid = new FluidTank(capacity);
    }

    @Override
    public int getTanks() {
        return fluid.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluid.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluid.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return fluid.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return fluid.fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }
}
