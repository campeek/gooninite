package net.cpeek.gooninite.blocks.machines.phase_destabilizer;

import net.cpeek.gooninite.blocks.machines.GooniniteBasicMachineBlockEntity;
import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.handlers.GoonFluidHandler;
import net.cpeek.gooninite.items.GooniniteItems;
import net.cpeek.gooninite.menus.PhaseDestabilizerMenu;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.cpeek.gooninite.recipes.PhaseDestabilizingRecipe;
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

public class PhaseDestabilizerBlockEntity extends GooniniteBasicMachineBlockEntity<PhaseDestabilizingRecipe> implements MenuProvider {
    public PhaseDestabilizerBlockEntity(BlockPos pos, BlockState state){
        super(GooniniteBlockEntities.PHASE_DESTABILIZER.get(), pos, state, 50000, 1);
    }

    public static final int SLOT_IN = 0;


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
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    private final GoonFluidHandler fluidHandler = new GoonFluidHandler(2000);


    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(()-> fluidHandler);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == ForgeCapabilities.FLUID_HANDLER && side == Direction.UP) {
            return fluidCap.cast();
        }
        return super.getCapability(cap, side);  // otherwise pass it on
    }

    @Override
    public void invalidateCaps() {
        super.itemCap.invalidate();
        super.energyCap.invalidate();
        fluidCap.invalidate();
    }

    @Override
    public Optional<PhaseDestabilizingRecipe> findRecipe() {
        if(level == null) return Optional.empty();

        SimpleContainer inv = new SimpleContainer(1);
        inv.addItem(itemHandler.getStackInSlot(SLOT_IN));

        return level.getRecipeManager().getRecipeFor(GooniniteRecipes.PHASE_DESTABILIZING_RECIPE.get(), inv, level);
    }


    protected boolean canDoWork(){
        if(currentRecipe == null) {
            //System.out.println("no recipe");
            return false; }// no recipe
        if(itemHandler.getStackInSlot(SLOT_IN).isEmpty()){
            //System.out.println("no item");
            return false; }// no item
        if(energyStorage.getEnergyStored() < currentRecipe.energy()){
            //System.out.println("no energy");
            return false; } // not enough energy

        ItemStack nugget = itemHandler.getStackInSlot(SLOT_IN);
        if (!nugget.is(GooniniteItems.GOONINITE_NUGGET_ITEM.get())) {
            //System.out.println("wrong item");
            return false;}

        return true;
    }

    @Override
    protected void consumeInputs() {
        assert currentRecipe != null;
        energyStorage.extractEnergy(currentRecipe.energy(), false);
    }

    @Override
    protected void createOutputs() {
        assert currentRecipe != null;
        fluidHandler.fill(currentRecipe.resultFluid(), IFluidHandler.FluidAction.EXECUTE);
        itemHandler.extractItem(SLOT_IN, 1, false);
    }

    @Override
    protected boolean canOutput() {
        assert currentRecipe != null;
        System.out.println(currentRecipe);
        return (fluidHandler.getFluidInTank(0).getAmount()+currentRecipe.resultFluid().getAmount()) <= fluidHandler.getTankCapacity(0);
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
