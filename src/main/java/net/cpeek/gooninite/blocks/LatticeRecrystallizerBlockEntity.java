package net.cpeek.gooninite.blocks;


import net.cpeek.gooninite.blocks.handlers.GoonFluidHandler;
import net.cpeek.gooninite.items.GooniniteItems;
import net.cpeek.gooninite.menus.LatticeRecrystallizerMenu;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.cpeek.gooninite.recipes.LatticeRecrystallizingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LatticeRecrystallizerBlockEntity extends GooniniteBasicMachineBlockEntity<LatticeRecrystallizingRecipe> implements MenuProvider {
    public LatticeRecrystallizerBlockEntity(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.LATTICE_RECRYSTALLIZER.get(), pos, state, 50000, 2);
    }

    // Resource Storage
    private final GoonFluidHandler fluidHandler = new GoonFluidHandler(20000);
    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(()->fluidHandler);

    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;

    // Container Data
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index){
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                case 4 -> fluidHandler.getFluidInTank(0).getAmount();
                case 5 -> fluidHandler.getTankCapacity(0);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index){
                case 0 -> progress = value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    @Override
    protected void consumeInputs() {
        assert currentRecipe != null;
        energyStorage.extractEnergy(currentRecipe.energy(), false);
        fluidHandler.drain(currentRecipe.ingredientFluid(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    protected boolean canOutput() {
        return itemHandler.getStackInSlot(SLOT_OUT).isEmpty();
    }

    @Override
    protected void serverTickHook(BlockPos pos, BlockState state) {

    }

    @Override
    protected void runningTickHook(BlockPos pos, BlockState state) {

    }

    @Override
    protected void startTickHook(BlockPos pos, BlockState state) {

    }

    @Override
    protected void endTickHook(BlockPos pos, BlockState state) {

    }

    @Override
    protected void createOutputs() {
        assert currentRecipe != null;
        ItemStack result = currentRecipe.resultItem();

        ItemStack in = itemHandler.getStackInSlot(SLOT_IN);
        ItemStack out = itemHandler.getStackInSlot(SLOT_OUT);
        in.shrink(1);

        itemHandler.setStackInSlot(SLOT_IN, in);

        if (out.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUT, result.copy());
        } else {
            out.grow(result.getCount());
            itemHandler.setStackInSlot(SLOT_OUT, out);
        }
    }


    @Override
    public Optional<LatticeRecrystallizingRecipe> findRecipe(){
        if(level == null) return Optional.empty();

        SimpleContainer inv = new SimpleContainer(1);
        inv.setItem(0, itemHandler.getStackInSlot(SLOT_IN));

        return level.getRecipeManager().getRecipeFor(
                GooniniteRecipes.LATTICE_RECRYSTALLIZING_RECIPE.get(), inv, level);
    }

    @Override
    public boolean canDoWork(){
        if (currentRecipe == null) return false; // no recipe
        if (itemHandler.getStackInSlot(SLOT_IN).isEmpty()) return false; // no item
        if (energyStorage.getEnergyStored() < currentRecipe.energy()) return false; // not enough energy
        if (fluidHandler.getFluidInTank(0).getAmount() < currentRecipe.ingredientFluid().getAmount()) return false; // not enough fluid
        ItemStack liner = itemHandler.getStackInSlot(SLOT_IN);
        if (liner.is(GooniniteItems.GOONINITE_LINER_ITEM.get())) {
            // only take broken liners
            return liner.getDamageValue() == liner.getMaxDamage();
        }
        return true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER && side == Direction.DOWN){
            return fluidCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidCap.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.gooninite.lattice_recrystallizer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new LatticeRecrystallizerMenu(id, inv, this, data);
    }

    public IItemHandler getItemHandler(){
        return itemHandler;
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.put("Tank0", fluidHandler.getFluidInTank(0).writeToNBT(new CompoundTag()));
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        if(tag.contains("Tank0")){
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("Tank0"));
            fluidHandler.setFluid(fluidStack);
        }
    }
}
