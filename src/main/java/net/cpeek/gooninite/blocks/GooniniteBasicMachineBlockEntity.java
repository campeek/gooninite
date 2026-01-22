package net.cpeek.gooninite.blocks;


import net.cpeek.gooninite.blocks.handlers.GoonEnergyStorage;
import net.cpeek.gooninite.recipes.BaseGoonRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class GooniniteBasicMachineBlockEntity <R extends BaseGoonRecipe> extends BlockEntity {

    public static final int SLOT_IN = 0;

    private boolean running;
    protected int progress;
    protected int maxProgress;

    protected @Nullable R currentRecipe;

    protected final GoonEnergyStorage energyStorage;
    protected final ItemStackHandler itemHandler;

    private final LazyOptional<IEnergyStorage> energyCap;
    private final LazyOptional<IItemHandler> itemCap;


    public GooniniteBasicMachineBlockEntity(BlockEntityType type, BlockPos pos, BlockState state, int energyCapacity, int itemCapacity){
        super(type, pos, state);
        energyStorage = new GoonEnergyStorage(energyCapacity, 10000, 5000);
        itemHandler = new ItemStackHandler(itemCapacity){
            @Override
            protected void onContentsChanged(int slot){
                if(slot == SLOT_IN){
                    if(running) {
                        running = false;
                        progress = 0;
                    }
                    handleChangeRecipe();
                }
                setChanged();
                if(level != null && !level.isClientSide) // maintain server authority
                    level.sendBlockUpdated(pos, state, state, 3);
            }
        };

        energyCap = LazyOptional.of(() -> energyStorage);
        itemCap = LazyOptional.of(() -> itemHandler);
    }

    public abstract Optional<R> findRecipe();
    protected void handleChangeRecipe(){
        var recipeOpt = findRecipe();

        if(recipeOpt.isPresent()){
            currentRecipe = recipeOpt.get();
            maxProgress = currentRecipe.processingTime;
            System.out.println("recipe changed: " + maxProgress);
        } else {
            currentRecipe = null;
            System.out.println("recipe cleared");
        }
    }

    protected abstract boolean canDoWork();
    protected abstract void consumeInputs();
    protected abstract void createOutputs();
    protected abstract boolean canOutput();

    protected abstract void serverTickHook(BlockPos pos, BlockState state);
    protected abstract void runningTickHook(BlockPos pos, BlockState state);
    protected abstract void startTickHook(BlockPos pos, BlockState state);
    protected abstract void endTickHook(BlockPos pos, BlockState state);


    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("Running", running);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.put("Items", itemHandler.serializeNBT());
        /*if(currentRecipe != null) {
            tag.putString("Recipe", currentRecipe.id.toString());
        } else {
            running = false;
            progress = 0;
        }*/
        saveExtra(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        itemHandler.deserializeNBT(tag.getCompound("Items"));
        running = tag.getBoolean("Running");
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        if(maxProgress == 0) maxProgress = 100;
        energyStorage.setEnergy(tag.getInt("Energy"));

        /*if(tag.contains("Recipe")){
            ResourceLocation recipeId = new ResourceLocation(tag.getString("Recipe"));
        }*/

        loadExtra(tag);
        //handleChangeRecipe();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        handleChangeRecipe();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if(tag != null){
            load(tag);
        }
    }

    // for saving/loading fluids or anything else
    protected abstract void saveExtra(CompoundTag tag);
    protected abstract void loadExtra(CompoundTag tag);


    public static void serverTick(Level level, BlockPos pos, BlockState state, GooniniteBasicMachineBlockEntity be){
        if(!be.running && be.canDoWork()){
            be.running = true;
            be.consumeInputs();
            be.startTickHook(pos, state);
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        if(be.running){
            be.progress++;
            if(be.progress >= be.maxProgress){
                if(be.canOutput()){
                    be.createOutputs();

                    be.running = false;
                    be.progress = 0;

                    be.endTickHook(pos, state);

                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                }
            }
            be.runningTickHook(pos, state);
        }
        be.serverTickHook(pos, state);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY && side == getBlockState().getValue(GooniniteMachineBlock.FACING).getOpposite()){
            return energyCap.cast();
        } else if (cap == ForgeCapabilities.ITEM_HANDLER){
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public int getProgress(){
        return progress;
    }
    public int getMaxProgress(){
        return maxProgress;
    }

    public int getEnergy(){
        return energyStorage.getEnergyStored();
    }
    public int getMaxEnergy(){
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public void invalidateCaps() {
        itemCap.invalidate();
        energyCap.invalidate();
    }
}
