package net.cpeek.gooninite.blocks.machines.mech_press;


import net.cpeek.gooninite.blocks.machines.GooniniteBasicMachineBlockEntity;
import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.menus.MechanicalPressMenu;
import net.cpeek.gooninite.recipes.GoonPressingRecipe;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

@SuppressWarnings("NullableProblems")
public class MechanicalSinteringPressBE extends GooniniteBasicMachineBlockEntity<GoonPressingRecipe> implements MenuProvider {

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
    public int totalTicks = 240;
    public int pistonMoveTicks = (int)(totalTicks*pistonRatio);
    public int pistonDwellTicks = (int) (totalTicks*dwellRatio);

    public float pistonSpeed = 1f/pistonMoveTicks;

    private PressPhase phase = PressPhase.IDLE;

    private int RPM = 0;
    private boolean heat = false;

    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index){
                case 0 -> progress;
                case 1 -> totalTicks;
                case 2 -> RPM;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index){
                case 0 -> progress = value;
                case 1 -> totalTicks = value;
                case 2 -> RPM = value;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };


    public MechanicalSinteringPressBE(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.PRESS_BE.get(), pos, state, 0, 2);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MechanicalSinteringPressBE be){
        // increase progress at a constant rate
    }

    @Override
    public Optional<GoonPressingRecipe> findRecipe(){
        if (level == null) return Optional.empty();

        SimpleContainer inv = new SimpleContainer(1);
        inv.setItem(0, itemHandler.getStackInSlot(SLOT_IN));

        return level.getRecipeManager().getRecipeFor(
                GooniniteRecipes.GOON_PRESSING_RECIPE.get(), inv, level);
    }


    @Override
    protected boolean canDoWork(){
        if(RPM < 1) return false;
        if(itemHandler.getStackInSlot(SLOT_IN).isEmpty()) return false;
        //if(!heat) return false;

        return true;
    }

    @Override
    protected void consumeInputs() {

    }

    @Override
    protected void createOutputs() {
        assert currentRecipe != null;
        ItemStack result = currentRecipe.resultItem();

        ItemStack in = itemHandler.getStackInSlot(SLOT_IN);
        ItemStack out = itemHandler.getStackInSlot(SLOT_OUT);
        itemHandler.extractItem(SLOT_IN, 1, false);

        if (out.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUT, result.copy());
        } else {
            out.grow(result.getCount());
            itemHandler.setStackInSlot(SLOT_OUT, out);
        }
    }

    @Override
    protected boolean canOutput() {
        return itemHandler.getStackInSlot(SLOT_OUT).isEmpty();
    }

    private void recomputeAnimTimes(){
        this.pistonMoveTicks = (int)(totalTicks*pistonRatio);
        this.pistonDwellTicks = (int)(totalTicks*dwellRatio);
        this.pistonSpeed = 1f/pistonMoveTicks;
    }

    private static double getTicksToComplete(int rpm, float k, float a, float b){
        double num = Math.pow(sigmoidFunc(rpm, k), a);
        double dom = num + Math.pow(1-sigmoidFunc(rpm, k),b);

        return (num/dom);
    }

    private static float sigmoidFunc(float x, float k){
        return (float)(1/(1+ Math.exp(k*(x-64))));
    }

    @Override
    protected void runningTickHook(BlockPos pos, BlockState state) {
        long currentTick = level.getGameTime();
        long phaseTicks = getTicksSinceLast();

        if(phaseTicks >= pistonMoveTicks && phase == PressPhase.DESCEND){
            phase = PressPhase.PRESS;
            lastPhaseChangeTick = currentTick;
            System.out.println("Phase change after " + pistonMoveTicks);
            System.out.println("Ticks spent in phase: " + phaseTicks);

            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        } else if(phaseTicks >= pistonDwellTicks && phase == PressPhase.PRESS){
            phase = PressPhase.ASCEND;
            lastPhaseChangeTick = currentTick;
            System.out.println("Phase change after " + pistonDwellTicks);
            System.out.println("Ticks spent in phase: " + phaseTicks);

            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
        else if(phaseTicks >= pistonMoveTicks && phase == PressPhase.ASCEND){
            phase = PressPhase.IDLE;
            lastPhaseChangeTick = currentTick;
            System.out.println("Phase change after " + pistonMoveTicks);
            System.out.println("Ticks spent in phase: " + phaseTicks);


            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    protected void startTickHook(BlockPos pos, BlockState state) {
        phase = PressPhase.DESCEND;
        cycleStartTick = level.getGameTime();
        lastPhaseChangeTick = cycleStartTick;

        updateTotalTicks();
        recomputeAnimTimes();
    }

    @Override
    protected void endTickHook(BlockPos pos, BlockState state) {

    }

    @Override
    protected void serverTickHook(BlockPos pos, BlockState state) {

    }



    /**
     * @return Ticks since last phase change
     */
    public long getTicksSinceLast(){
        if(level == null) return 0;
        return level.getGameTime() - lastPhaseChangeTick;
    }

    private void updateTotalTicks() {
        if(currentRecipe != null){
            totalTicks = (int)(currentRecipe.processingTime()*getTicksToComplete(RPM, k, this.dimReturnFactor, this.rampUpFactor)+minimumTicks);
            currentRecipe.processingTime = totalTicks;
            maxProgress = totalTicks;
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

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putLong("CycleStart", cycleStartTick);
        tag.putLong("LastPhase", lastPhaseChangeTick);
        tag.putString("Phase", phase.name());
        tag.putInt("RPM", RPM);
        tag.putInt("PistonTicks", pistonMoveTicks);
        tag.putInt("DwellTicks", pistonDwellTicks);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
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
        recomputeAnimTimes();
        //}
    }


    // boring shit
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.gooninite.mechanical_press");
    }
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player){
        return new MechanicalPressMenu(id, inv, this, data);
    }
    public int getRPM() {
        return this.RPM;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    public IItemHandler getItemHandler(){
        return itemHandler;
    }
    public int getProgress(){
        return progress;
    }

    @Override
    public void invalidateCaps() {
        super.energyCap.invalidate();
        super.itemCap.invalidate();
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
