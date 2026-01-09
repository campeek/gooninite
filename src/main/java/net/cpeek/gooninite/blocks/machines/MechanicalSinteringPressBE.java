package net.cpeek.gooninite.blocks.machines;


import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.items.GooniniteItems;
import net.cpeek.gooninite.menus.MechanicalPressMenu;
import net.cpeek.gooninite.recipes.GoonPressingRecipe;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("NullableProblems")
public class MechanicalSinteringPressBE extends BlockEntity implements MenuProvider {

    private int progress = 0; // how far along the process is

    private boolean running;
    private long cycleStartTick;
    private long lastPhaseChangeTick;

    private final int minimumTicks = 120;       // the machine will never run faster than this despite rpm
                                                // ticks @ 64 rpm is defined in recipe

    private final float rampUpFactor = 2.1f;    // controls how steep the ramp-up is (lower values - more linear)
    private final float dimReturnFactor = 2.9f; // controls how steep the dim-returns ramp is (lower values - more linear
    private final float k = 0.023f;             // controls how steep the rpm/ticks slope is (lower values - flatter slope)

    private final float pistonRatio = 0.4f;     // ratios for animation timing
    private final float dwellRatio = 0.2f;      // these control what percentage of the total time is taken
                                                // by these animations. they should probably add up to 1 lol
                                                // multiply pistonRatio by 2 cuz it moves twice



    private final int tickrate = 20;

    public int totalTicks = 240;
    public int pistonMoveTicks = (int)(totalTicks*pistonRatio);
    public int pistonDwellTicks = (int) (totalTicks*dwellRatio);

    public float pistonSpeed = 1f/pistonMoveTicks;

    private PressPhase phase = PressPhase.IDLE;

    private int RPM = 0;
    private boolean heat = false;

    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;

    private final ItemStackHandler items = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(level != null && !level.isClientSide){ // keep server authoritative
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot == SLOT_OUT) return false;
            return stack.is(GooniniteItems.GOONINITE_NUGGET_ITEM.get());
        }
    };

    private LazyOptional<IItemHandler> itemCap = LazyOptional.empty();

    public MechanicalSinteringPressBE(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.PRESS_BE.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MechanicalSinteringPressBE be){
        // increase progress at a constant rate
    }

    private Optional<GoonPressingRecipe> getCurrentRecipe(){
        if (level == null) return Optional.empty();

        SimpleContainer inv = new SimpleContainer(1);
        inv.setItem(0, items.getStackInSlot(SLOT_IN));

        return level.getRecipeManager().getRecipeFor(
                GooniniteRecipes.GOON_PRESSING_RECIPE.get(), inv, level);
    }

    private boolean canDoWork(){
        if(RPM < 1) return false;
        if(items.getStackInSlot(SLOT_IN).isEmpty()) return false;
        //if(!heat) return false;

        return true;
    }

    private void recomputeAnimTimes(){
        this.pistonMoveTicks = (int)(totalTicks*pistonRatio);
        this.pistonDwellTicks = (int)(totalTicks*dwellRatio);
        this.pistonSpeed = 1f/pistonMoveTicks;

        /*System.out.println("Animation times computed");
        System.out.println("Move ticks: " + pistonMoveTicks);
        System.out.println("Dwell ticks: "+ pistonDwellTicks);
        System.out.println("Piston speed: " + pistonSpeed);*/
    }

    private static double getTicksToComplete(int rpm, float k, float a, float b){
        double num = Math.pow(sigmoidFunc(rpm, k), a);
        double dom = num + Math.pow(1-sigmoidFunc(rpm, k),b);

        return (num/dom);
    }

    private static float sigmoidFunc(float x, float k){
        return (float)(1/(1+ Math.exp(k*(x-64))));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MechanicalSinteringPressBE be){

        var recipeOptional = be.getCurrentRecipe();

        if(recipeOptional.isEmpty()){
            be.progress = 0;
            return;
        }

        GoonPressingRecipe recipe = recipeOptional.get();

        if(be.canDoWork()){
            ItemStack out = be.items.getStackInSlot(SLOT_OUT);
            ItemStack result = recipe.result();

            if(!out.isEmpty() && (!ItemStack.isSameItemSameTags(out, result) || out.getCount() >= out.getMaxStackSize())) return;

            if(!be.running && be.shouldStartCycle()) {
                be.running = true;
                be.phase = PressPhase.DESCEND;
                be.progress = 0;
                be.cycleStartTick = level.getGameTime();
                be.lastPhaseChangeTick = be.cycleStartTick;

                be.updateTotalTicks();
                be.recomputeAnimTimes();

                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);

            }
            if(be.running){
                be.progress++;
                long currentTick = level.getGameTime();
                long phaseTicks = be.getTicksSinceLast();

                // phase handling for animation + effects hooking
                // press gets put into DESCEND phase when it starts running
                if(phaseTicks >= be.pistonMoveTicks && be.phase == PressPhase.DESCEND){
                    be.phase = PressPhase.PRESS;
                    be.lastPhaseChangeTick = currentTick;
                    System.out.println("Phase change after " + be.pistonMoveTicks);
                    System.out.println("Ticks spent in phase: " + phaseTicks);

                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                } else if(phaseTicks >= be.pistonDwellTicks && be.phase == PressPhase.PRESS){
                    be.phase = PressPhase.ASCEND;
                    be.lastPhaseChangeTick = currentTick;
                    System.out.println("Phase change after " + be.pistonDwellTicks);
                    System.out.println("Ticks spent in phase: " + phaseTicks);

                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                }
                else if(phaseTicks >= be.pistonMoveTicks && be.phase == PressPhase.ASCEND){
                    be.phase = PressPhase.IDLE;
                    be.lastPhaseChangeTick = currentTick;
                    System.out.println("Phase change after " + be.pistonMoveTicks);
                    System.out.println("Ticks spent in phase: " + phaseTicks);

                    if(be.progress >= be.totalTicks){
                        ItemStack in = be.items.getStackInSlot(SLOT_IN);
                        in.shrink(1);
                        be.items.setStackInSlot(SLOT_IN, in);

                        if(out.isEmpty()){
                            be.items.setStackInSlot(SLOT_OUT, result.copy());
                        } else {
                            out.grow(result.getCount());
                            be.items.setStackInSlot(SLOT_OUT, out);
                        }
                    }

                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    be.running = false;
                    be.progress = 0;

                }
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        itemCap = LazyOptional.of(() -> items);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) return itemCap.cast();
        return super.getCapability(cap, side);
    }

    private boolean shouldStartCycle(){
        return !running && canDoWork();
    }

    /**
     * @return Ticks since last phase change
     */
    public long getTicksSinceLast(){
        if(level == null) return 0;
        return level.getGameTime() - lastPhaseChangeTick;
    }
    public long getCycleStartTick(){
        if(level == null) return 0;
        return cycleStartTick;
    }


    // client-server sync events
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Progress", progress);
        tag.putBoolean("Running", running);
        tag.putLong("CycleStart", cycleStartTick);
        tag.putLong("LastPhase", lastPhaseChangeTick);
        tag.putString("Phase", phase.name());
        tag.put("Items", items.serializeNBT());
        tag.putInt("PistonTicks", pistonMoveTicks);
        tag.putInt("DwellTicks", pistonDwellTicks);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("Progress");
        running = tag.getBoolean("Running");
        cycleStartTick = tag.getLong("CycleStart");
        lastPhaseChangeTick = tag.getLong("LastPhase");
        phase = PressPhase.valueOf(tag.getString("Phase"));
        if(tag.contains("Items")) {
            items.deserializeNBT(tag.getCompound("Items"));
        }
        pistonMoveTicks = tag.getInt("PistonMoveTicks");
        pistonDwellTicks = tag.getInt("PistonDwellTicks");
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Progress", progress);
        tag.putBoolean("Running", running);
        tag.putLong("CycleStart", cycleStartTick);
        tag.putLong("LastPhase", lastPhaseChangeTick);
        tag.putString("Phase", phase.name());
        tag.putInt("RPM", RPM);
        tag.putInt("PistonTicks", pistonMoveTicks);
        tag.putInt("DwellTicks", pistonDwellTicks);
        return tag;
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        progress = tag.getInt("Progress");
        running = tag.getBoolean("Running");
        cycleStartTick = tag.getLong("CycleStart");
        lastPhaseChangeTick = tag.getLong("LastPhase");
        phase = PressPhase.valueOf(tag.getString("Phase"));
        pistonMoveTicks = tag.getInt("PistonTicks");
        pistonDwellTicks = tag.getInt("DwellTicks");

        //int oldRPM = RPM;
        RPM = tag.getInt("RPM");
        //if(oldRPM != RPM){
            System.out.println("Updating RPM from block update");
            //System.out.println("Old RPM: " + oldRPM);
            System.out.println("New RPM:"  + RPM);
            System.out.println("New Piston Ticks: " + pistonMoveTicks);

            updateTotalTicks();
            //recomputeAnimTimes();
        //}
    }

    private void updateTotalTicks() {
        if(getCurrentRecipe().isPresent()){
            GoonPressingRecipe recipe = getCurrentRecipe().get();
            totalTicks = (int)(recipe.processingTime()*getTicksToComplete(RPM, k, this.dimReturnFactor, this.rampUpFactor)+minimumTicks);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection con, ClientboundBlockEntityDataPacket packet){
        CompoundTag tag = packet.getTag();
        if(tag != null) handleUpdateTag(tag);
    }


    // boring shit
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.gooninite.mechanical_press");
    }
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player){
        return new MechanicalPressMenu(id, inv, this);
    }
    public int getRPM() {
        return this.RPM;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    public IItemHandler getItemHandler(){
        return items;
    }
    public int getProgress(){
        return progress;
    }
    public PressPhase getPhase(){
        return phase;
    }
    public void setRPM(int rpm){
        this.RPM = rpm;
        updateTotalTicks();
        recomputeAnimTimes();
        System.out.println("RPM Updated: " + rpm);

        // update clients
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
}
