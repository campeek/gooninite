package net.cpeek.gooninite.blocks.handlers;


import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class GoonFluidHandler implements IFluidHandler {

    private final FluidTank fluidTank;

    public GoonFluidHandler(int capacity){
        fluidTank = new FluidTank(capacity);
    }

    @Override
    public int getTanks() {
        return fluidTank.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluidTank.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidTank.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return fluidTank.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return fluidTank.fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if(resource.isEmpty()) return FluidStack.EMPTY;
        if(fluidTank.isEmpty()) return FluidStack.EMPTY;
        if(!fluidTank.getFluid().isFluidEqual(resource)) return FluidStack.EMPTY;

        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if(maxDrain <= 0) return FluidStack.EMPTY;
        if(fluidTank.isEmpty()) return FluidStack.EMPTY;

        int drainedAmt = Math.min(maxDrain, fluidTank.getFluid().getAmount());
        FluidStack drained = new FluidStack(fluidTank.getFluid(), drainedAmt);

        if(action.execute()){
            fluidTank.getFluid().shrink(drainedAmt);
            if(fluidTank.getFluid().getAmount() <= 0){
                fluidTank.setFluid(FluidStack.EMPTY);
            }
        }
        return drained;
    }

    public void setFluid(FluidStack fluidStack) {
        fluidTank.setFluid(fluidStack);
    }
}
