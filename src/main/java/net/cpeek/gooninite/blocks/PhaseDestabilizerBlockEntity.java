package net.cpeek.gooninite.blocks;

import net.cpeek.gooninite.fluids.GoonFluidHandler;
import net.cpeek.gooninite.items.GooniniteItems;
import net.cpeek.gooninite.menus.PhaseDestabilizerMenu;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.cpeek.gooninite.recipes.LatticeRecrystallizingRecipe;
import net.cpeek.gooninite.recipes.PhaseDestabilizingRecipe;
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
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PhaseDestabilizerBlockEntity extends BlockEntity implements MenuProvider {
    public PhaseDestabilizerBlockEntity(BlockPos pos, BlockState state){
        super(GooniniteBlockEntities.PHASE_DESTABILIZER.get(), pos, state);
    }

    private final EnergyStorage energyStorage = new EnergyStorage(
            100000,     // capacity
            5000,               // max receive
            0                   // max extract (0=input only)
    );

    private final FluidTank fluid = new FluidTank(2000);

    public static final int SLOT_IN = 0;

    private PhaseDestabilizingRecipe currentRecipe;

    private int progress = 0;
    private int maxProgress = 100;

    private boolean running;

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

    private final ItemStackHandler itemHandler = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot){
            if(slot == SLOT_IN){
                if(!getStackInSlot(slot).isEmpty()){
                    Optional<PhaseDestabilizingRecipe> recipeOpt = getCurrentRecipe();
                    if(recipeOpt.isPresent()){
                        currentRecipe = recipeOpt.get();
                        maxProgress = currentRecipe.processingTime();
                    } else {
                        System.out.println("recipe not loaded");
                    }
                } else {
                    currentRecipe = null;
                }
            }
            setChanged();
        }
    };

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energyStorage);
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

    private Optional<PhaseDestabilizingRecipe> getCurrentRecipe(){
        if(level == null) return Optional.empty();

        SimpleContainer inv = new SimpleContainer(1);
        inv.setItem(0, itemHandler.getStackInSlot(SLOT_IN));

        return level.getRecipeManager().getRecipeFor(
                GooniniteRecipes.PHASE_DESTABILIZING_RECIPE.get(), inv, level);
    }

    private boolean canDoWork(){
        //if(currentRecipe == null) return false; // no recipe
        if(itemHandler.getStackInSlot(SLOT_IN).isEmpty()) return false; // no item
        if(energyStorage.getEnergyStored() < currentRecipe.energy()) return false; // not enough energy
        if(fluidHandler.getFluidInTank(0).getAmount() < currentRecipe.fluid()) return false; // not enough fluid
        ItemStack nugget = itemHandler.getStackInSlot(SLOT_IN);
        if (nugget.is(GooniniteItems.GOONINITE_NUGGET_ITEM.get())) {
            if(nugget.getDamageValue() != nugget.getMaxDamage()){ // only take broken liners
                return false;
            }
        }

        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PhaseDestabilizerBlockEntity be){

        if(!be.running && be.canDoWork()){
            be.running = true;
            System.out.println("starting process");
            be.energyStorage.extractEnergy(be.currentRecipe.energy(), false);
            be.fluidHandler.drain(be.currentRecipe.fluid(), IFluidHandler.FluidAction.EXECUTE);
        }

        /*if(be.running){
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
        }*/
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
