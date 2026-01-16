package net.cpeek.gooninite.blocks;


import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.fluids.GoonFluidHandler;
import net.cpeek.gooninite.items.GooniniteItems;
import net.cpeek.gooninite.menus.LatticeRecrystallizerMenu;
import net.cpeek.gooninite.menus.PhaseDestabilizerMenu;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.cpeek.gooninite.recipes.LatticeRecrystallizingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LatticeRecrystallizerBlockEntity extends BlockEntity implements MenuProvider {
    public LatticeRecrystallizerBlockEntity(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.LATTICE_RECRYSTALLIZER.get(), pos, state);
    }

    // Resource Storage
    private final GoonFluidHandler fluidHandler = new GoonFluidHandler(20000);
    private final ItemStackHandler itemHandler = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            if(slot == SLOT_IN){
                if(!getStackInSlot(slot).isEmpty()){
                    Optional<LatticeRecrystallizingRecipe> recipeOpt = getCurrentRecipe();
                    if(recipeOpt.isPresent()){
                        currentRecipe = recipeOpt.get();
                        maxProgress = currentRecipe.processsingTime();
                    } else {
                        System.out.println("recipe not loaded");
                    }
                } else {
                    currentRecipe = null;
                }
            }
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot == SLOT_OUT) return false;
            return stack.is(GooniniteItems.GOONINITE_LINER_ITEM.get());
        }
    };
    private final EnergyStorage energyStorage = new EnergyStorage(50000, 5000, 0);


    // Forge Capabilities
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(()->energyStorage);
    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(()->fluidHandler);
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(()->itemHandler);


    // Machine Fields
    private int progress = 0;
    private int maxProgress = 520;

    private boolean running = false;

    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;

    private LatticeRecrystallizingRecipe currentRecipe;


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


    public static void serverTick(Level level, BlockPos pos, BlockState state, LatticeRecrystallizerBlockEntity be){

        if(!be.running && be.canDoWork()){
            be.running = true;
            System.out.println("starting process");
            be.energyStorage.extractEnergy(be.currentRecipe.energy(), false);
            be.fluidHandler.drain(be.currentRecipe.fluid(), IFluidHandler.FluidAction.EXECUTE);
        }

        // TODO: stall progress if output is full
        if(be.running){
            be.progress++;
            if(be.progress >= be.maxProgress){
                if(be.itemHandler.getStackInSlot(SLOT_OUT).isEmpty()) {
                    be.running = false;
                    be.progress = 0;

                    ItemStack result = be.currentRecipe.result();

                    ItemStack in = be.itemHandler.getStackInSlot(SLOT_IN);
                    ItemStack out = be.itemHandler.getStackInSlot(SLOT_OUT);
                    in.shrink(1);

                    be.itemHandler.setStackInSlot(SLOT_IN, in);

                    if (out.isEmpty()) {
                        be.itemHandler.setStackInSlot(SLOT_OUT, result.copy());
                    } else {
                        out.grow(result.getCount());
                        be.itemHandler.setStackInSlot(SLOT_OUT, out);
                    }
                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                }
            }
        }
    }

    private Optional<LatticeRecrystallizingRecipe> getCurrentRecipe(){
        if(level == null) return Optional.empty();

        SimpleContainer inv = new SimpleContainer(1);
        inv.setItem(0, itemHandler.getStackInSlot(SLOT_IN));

        return level.getRecipeManager().getRecipeFor(
                GooniniteRecipes.LATTICE_RECRYSTALLIZING_RECIPE.get(), inv, level);
    }

    private boolean canDoWork(){
        if(currentRecipe == null) return false; // no recipe
        if(itemHandler.getStackInSlot(SLOT_IN).isEmpty()) return false; // no item
        if(energyStorage.getEnergyStored() < currentRecipe.energy()) return false; // not enough energy
        if(fluidHandler.getFluidInTank(0).getAmount() < currentRecipe.fluid()) return false; // not enough fluid
        ItemStack liner = itemHandler.getStackInSlot(SLOT_IN);
        if (liner.is(GooniniteItems.GOONINITE_LINER_ITEM.get())) {
            if(liner.getDamageValue() != liner.getMaxDamage()){ // only take broken liners
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        Direction relBack = getBlockState().getValue(LatticeRecrystallizerBlock.FACING).getOpposite();

        if(cap == ForgeCapabilities.ENERGY
                && side == relBack){
            return energyCap.cast();
        }
        else if(cap == ForgeCapabilities.FLUID_HANDLER && side == Direction.DOWN){
            return fluidCap.cast();
        }
        else if(cap == ForgeCapabilities.ITEM_HANDLER && side != Direction.DOWN && side != relBack){
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
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
}
