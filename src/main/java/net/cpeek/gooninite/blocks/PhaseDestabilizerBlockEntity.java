package net.cpeek.gooninite.blocks;

import net.cpeek.gooninite.menus.PhaseDestabilizerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhaseDestabilizerBlockEntity extends BlockEntity implements MenuProvider {
    public PhaseDestabilizerBlockEntity(BlockPos pos, BlockState state){
        super(GooniniteBlockEntities.HYPERBOLIC_GOON_CHAMBER.get(), pos, state);
    }

    private final EnergyStorage energy = new EnergyStorage(
            100000,     // capacity
            5000,               // max receive
            0                   // max extract (0=input only)
    );

    private final FluidTank fluid = new FluidTank(2000);

    private int progress = 0;
    private int maxProgress = 100;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index){
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energy.getEnergyStored();
                case 3 -> energy.getMaxEnergyStored();
                default -> 0;
            };
        }


        @Override
        public void set(int index, int value) {

        }

        @Override
        public int getCount() {
            return 0;
        }
    };

    private final IFluidHandler fluidHandler = new IFluidHandler() {
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
    };

    private final ItemStackHandler itemHandler = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
        }
    };

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(()-> fluidHandler);
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> itemHandler);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(
        @NotNull Capability<T> cap,
        @Nullable Direction side
    ){
        if(cap == ForgeCapabilities.ENERGY      // requesting energy capabilities
                && side == getBlockState().getValue(PhaseDestabilizerBlock.FACING).getOpposite()){ // where the requested capability is allowed to hook up
            return energyCap.cast();                                                                   // opposite of the direction the block is facing = the back
        } else if(cap == ForgeCapabilities.FLUID_HANDLER
                        && side == Direction.UP){
            return fluidCap.cast();
        } else if(cap == ForgeCapabilities.ITEM_HANDLER
                        && side == Direction.DOWN){
            return itemCap.cast();
        }
        return super.getCapability(cap, side);  // otherwise pass it on
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("block.gooninite.phase_destabilizer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new PhaseDestabilizerMenu(id, inv, this, data);
    }

    public IItemHandler getItemHandler(){
        return itemHandler;
    }
}
