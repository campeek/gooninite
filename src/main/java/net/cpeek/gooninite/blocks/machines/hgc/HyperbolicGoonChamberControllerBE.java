package net.cpeek.gooninite.blocks.machines.hgc;


import net.cpeek.gooninite.GooniniteSounds;
import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.blocks.GooniniteTags;
import net.cpeek.gooninite.blocks.handlers.GoonEnergyStorage;
import net.cpeek.gooninite.blocks.handlers.GoonFluidHandler;
import net.cpeek.gooninite.items.GooniniteItems;
import net.cpeek.gooninite.menus.HyperbolicGoonChamberMenu;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.cpeek.gooninite.recipes.HyperbolicGoonificationRecipe;
import net.cpeek.gooninite.sounds.GoonChamberSound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberPartBlock.FORMED;


public class HyperbolicGoonChamberControllerBE extends BlockEntity implements MenuProvider {

    private boolean recipeLocked = false;

    private final ItemStackHandler itemHandler;
    private final IItemHandler pipeHandler;
    private final GoonEnergyStorage energyStorage;

    private HyperbolicGoonificationRecipe currentRecipe;

    public HyperbolicGoonChamberControllerBE(BlockPos pos, BlockState state){
        super(GooniniteBlockEntities.HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get(), pos, state);
        itemHandler = new ItemStackHandler(6){

            /*@Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if(slot < 4) return ItemStack.EMPTY; // don't extract input pellets
                return super.extractItem(slot, amount, simulate);
            }*/

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if(slot>=0 && slot < 4){
                    //System.out.println(stack);
                    return stack.is(GooniniteItems.GOONINITE_PELLET_ITEM.get()) || stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
                } else if(slot == 4){
                    return stack.is(GooniniteItems.GOONINITE_LINER_ITEM.get());
                } else if(slot == 5){
                    return false;
                }
                return false;
            }

            @Override
            protected void onContentsChanged(int slot){
                if(level == null) return;
                if(level.isClientSide) return;
                if(slot < 5){
                    if(phase.getIndex() == GoonChamberPhase.CHARGING.getIndex()) {
                        changePhase(GoonChamberPhase.COOLDOWN);
                        //System.out.println("cycle failed - recipe change");
                        level.playSound(null, worldPosition,
                                GooniniteSounds.GOON_CHAMBER_SPIN_DOWN.get(),
                                SoundSource.BLOCKS,
                                1.0f,
                                0.95f+level.random.nextFloat()*0.1f);
                    }
                    handleChangeRecipe();

                }
                setChanged();
                if(level != null && !level.isClientSide) // maintain server authority
                    level.sendBlockUpdated(pos, state, state, 3);
            }
        };
        pipeHandler = new IItemHandlerModifiable() {
            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                itemHandler.setStackInSlot(slot, stack);
            }
            @Override
            public int getSlots() {
                return itemHandler.getSlots();
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return itemHandler.getStackInSlot(slot);
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if(slot > 5) return ItemStack.EMPTY;
                return itemHandler.insertItem(slot, stack, simulate);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if(slot <= 5) return ItemStack.EMPTY;
                return itemHandler.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return itemHandler.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return itemHandler.isItemValid(slot, stack);
            }
        };

        energyStorage = new GoonEnergyStorage(25000000, 300000000, 300000000);
    }

    private final GoonFluidHandler fluidHandler = new GoonFluidHandler(4000);

    private GoonChamberPhase phase = GoonChamberPhase.IDLE;
    //private boolean running = false;
    private long lastPhaseChangeTick;

    // TODO: get this from recipe
    private static final long MIN_ENERGY_PER_TICK = (528000000/11)/20;

    private static final long DISCHARGE_TIME = 80; // ticks to spend discharging (20 ticks/s)
    private static final long COOLDOWN_TIME = 80; // ticks to spend cooling down after process

    private static final int DISCHARGE_CHARGE_DECAY = (528000000/3)/20;
    private static final int FAILURE_CHARGE_DECAY = (528000000/2)/20;

    private ArrayList<BlockPos> blocks = new ArrayList<>();
    private BlockPos corePos;
    private BlockPos powerPortPos;
    private BlockPos fluidPortPos;

    private boolean needsValidated = false;

    private int currentCharge = 0;
    private int maxCharge = 528000000;

    private final BlockPos[] offsets = {
            new BlockPos(-1,1,0),
            new BlockPos(0,1,0),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(0,0,0)
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch(pIndex){
                case 0 -> currentCharge;
                case 1 -> maxCharge;
                case 2 -> phase.ordinal();
                case 3 -> (energyStorage.getEnergyStored() >> 16) & 0xffff; // high bits
                case 4 -> energyStorage.getEnergyStored() & 0xffff; // low bits
                case 5 -> (energyStorage.getMaxEnergyStored() >> 16) & 0xffff; // high bits (max)
                case 6 -> energyStorage.getMaxEnergyStored() & 0xffff; // low bits (max)
                case 7 -> fluidHandler.getFluidInTank(0).getAmount();
                case 8 -> fluidHandler.getTankCapacity(0);
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {

        }

        @Override
        public int getCount() {
            return 9;
        }
    };


    private BlockPos rotate(BlockPos pos, Direction dir){
        return switch(dir){
            case NORTH -> pos;
            case SOUTH -> new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
            case EAST -> new BlockPos(-pos.getZ(), pos.getY(), -pos.getX());
            case WEST -> new BlockPos(pos.getZ(), pos.getY(), pos.getX());
            default -> pos;
        };
    }

    /**
     * Checks multiblock form and updates parts
     */
    public void update(){
        if(level != null && level.isClientSide) return;
        if(!needsValidated) return;
        boolean valid = checkForm();
        boolean formed = getFormed();

        if(!formed){
            if(valid){
                makeMultiblock();
            }
        } else {
            if(!valid){
                breakMultiblock();
            }
        }
    }

    private boolean getFormed() {
        return getBlockState().getValue(FORMED);
    }

    private void makeMultiblock(){
        if(level == null) return;
        if(level.isClientSide) return;
        level.setBlock(getBlockPos(), getBlockState().setValue(FORMED, true), 3);
        for (BlockPos p : blocks) {
            BlockState state = level.getBlockState(p);
            if (state.is(GooniniteTags.Blocks.GOON_CHAMBER_BLOCKS)) {
                if (state.getBlock() instanceof HyperbolicGoonChamberPartBlock block) {
                    block.form(level, p, state);
                }

                BlockEntity be = level.getBlockEntity(p);
                if (be instanceof ChamberPartBlockEntity part) {
                    part.makeChamber(getBlockPos());
                }
            }
        }
        setChanged();
    }

    public void breakMultiblock() {
        if(level == null) return;
        if(level.isClientSide) return;

        // BLOCKSTATES ARE IMMUTABLE
        // BLOCKSTATES ARE IMMUTABLE
        // BLOCKSTATES ARE IMMUTABLE
        // BLOCKSTATES ARE IMMUTABLE
        // BLOCKSTATES ARE IMMUTABLE
        level.setBlock(getBlockPos(), getBlockState().setValue(FORMED, false), 3);
        cleanupMultiblockStates();

        //isFormed = false;
        corePos = null;
        powerPortPos = null;
        fluidPortPos = null;
        blocks.clear();
    }

    public boolean checkForm(){
        if(level == null) return false;
        if(level.isClientSide) return false;

        if(!blocks.isEmpty()){
            for(BlockPos p : blocks){
                if(!level.getBlockState(p).is(GooniniteTags.Blocks.GOON_CHAMBER_BLOCKS)){
                    return false;
                }
            }
        }

        for(int layer = 0; layer <= 2; layer++){
            for(BlockPos offset : offsets){
                offset = offset.offset(0,0,layer);
                if(!offset.equals(offsets[8])) {
                    offset = rotate(offset, getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
                    BlockPos pos = worldPosition.offset(offset);
                    if(!level.getBlockState(pos).is(GooniniteTags.Blocks.GOON_CHAMBER_BLOCKS)){
                        return false; // if even one block isnt tagged, fail
                    }
                    if(offset.equals(rotate(new BlockPos(0,0,1), getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)))){
                        if(!level.getBlockState(pos).is(GooniniteBlocks.HYPERBOLIC_GOON_CHAMBER_CORE.get())){
                            return false;
                        } else {
                            corePos = pos;
                        }
                    }

                    if(level.getBlockState(pos).is(GooniniteBlocks.GOON_FLUID_PORT.get())){
                        if(fluidPortPos == null){
                            fluidPortPos = pos;
                        }
                    }

                    if(level.getBlockState(pos).is(GooniniteBlocks.GOON_POWER_PORT.get())){
                        if(powerPortPos == null){
                            powerPortPos = pos;
                        }
                    }
                    if(!blocks.contains(pos))
                        blocks.add(pos);
                }
            }
        }
        if(fluidPortPos == null || powerPortPos == null) {
            return false;
        }
        //setChanged();
        //level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        return true;
    }

    protected void handleChangeRecipe() {
        if(level != null && level.isClientSide) return;
        if(!recipeLocked){
            var recipeOpt = findRecipe();

            if(recipeOpt.isPresent()){
                currentRecipe = recipeOpt.get();
            } else {
                currentRecipe = null;
            }
        }

    }

    public Optional<HyperbolicGoonificationRecipe> findRecipe() {
        if(level == null) return Optional.empty();

        SimpleContainer inv = new SimpleContainer(4);
        inv.addItem(itemHandler.getStackInSlot(0));
        inv.addItem(itemHandler.getStackInSlot(1));
        inv.addItem(itemHandler.getStackInSlot(2));
        inv.addItem(itemHandler.getStackInSlot(3));

        return level.getRecipeManager().getRecipeFor(GooniniteRecipes.GOONIFICATION_RECIPE.get(), inv, level);
    }

    protected boolean canDoWork() {
        if(!getFormed()) {
            return false;
        }
        if(phase != GoonChamberPhase.IDLE) {
            return false;
        }
        if(!slotsFull()) {
            return false;
        }
        if(!checkLiner()) {
            return false;
        }
        if(energyStorage.getEnergyStored() == 0){
            return false;
        }
        if(currentRecipe == null){
            return false;
        }
        if(fluidHandler.getFluidInTank(0).getAmount() < currentRecipe.ingredientFluid().getAmount()){
            return false;
        }


        return true;
    }

    public boolean slotsFull(){
        if(itemHandler.getStackInSlot(0).isEmpty()) return false;
        if(itemHandler.getStackInSlot(1).isEmpty()) return false;
        if(itemHandler.getStackInSlot(2).isEmpty()) return false;
        if(itemHandler.getStackInSlot(3).isEmpty()) return false;

        return true;
    }

    private boolean checkLiner(){
        ItemStack liner = itemHandler.getStackInSlot(4);
        if (liner.is(GooniniteItems.GOONINITE_LINER_ITEM.get())) {
            if(liner.getDamageValue() < liner.getMaxDamage()){
                return true;
            }
        }
        return false;
    }

    protected void consumeInputs() {
        if(currentRecipe!=null)
            fluidHandler.drain(currentRecipe.ingredientFluid(), IFluidHandler.FluidAction.EXECUTE);

    }

    protected void createOutputs() {
        /*itemHandler.extractItem(0, 1, false);
        itemHandler.extractItem(1, 1, false);
        itemHandler.extractItem(2, 1, false);
        itemHandler.extractItem(3, 1, false);*/

        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        itemHandler.setStackInSlot(1, ItemStack.EMPTY);
        itemHandler.setStackInSlot(2, ItemStack.EMPTY);
        itemHandler.setStackInSlot(3, ItemStack.EMPTY);

        if(canOutput()){
            if(currentRecipe!=null) {
                itemHandler.setStackInSlot(5, currentRecipe.resultItem());
                ItemStack liner = itemHandler.getStackInSlot(4);
                int damage = liner.getDamageValue();
                int max = liner.getMaxDamage();

                liner.setDamageValue(Math.min(damage+1, max));
                recipeLocked = false;
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    protected boolean canOutput() {
        return itemHandler.getStackInSlot(5).isEmpty();
    }

    private boolean isRunning(){
        return phase.getIndex() > GoonChamberPhase.IDLE.getIndex();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HyperbolicGoonChamberControllerBE be){
        if(!be.isRunning() && be.canDoWork()){
            be.startTickHook(pos, state);
            be.consumeInputs();

            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        } else if(be.isRunning()){
            be.runningTickHook(pos, state);
        }
        be.serverTickHook(pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, HyperbolicGoonChamberControllerBE be){
        if(!be.isRunning() && be.canDoWork()){
            be.startTickClient(pos, state);
        }
    }

    protected void serverTickHook(BlockPos pos, BlockState state) {
        if(needsValidated){
            update();
            needsValidated = false;
        }
    }

    protected void runningTickHook(BlockPos pos, BlockState state) {
        if(phase == GoonChamberPhase.CHARGING){
            int energyExtracted = energyStorage.extractEnergy((int) MIN_ENERGY_PER_TICK, false);
            if(energyExtracted >= MIN_ENERGY_PER_TICK){
                currentCharge+=energyExtracted;
            } else {
                // cycle fails because of energy throughput
                //System.out.println("cycle failed because of energy thruput");
                changePhase(GoonChamberPhase.COOLDOWN);
                level.playSound(null, corePos,
                        GooniniteSounds.GOON_CHAMBER_SPIN_DOWN.get(),
                        SoundSource.BLOCKS,
                        1.0f,
                        0.95f+level.random.nextFloat()*0.1f);
            }
            if(currentCharge >= maxCharge){
                // cycle succeeds
                changePhase(GoonChamberPhase.DISCHARGING);
                recipeLocked = true;
                level.playSound(null, corePos,
                        GooniniteSounds.GOON_CHAMBER_DISCHARGE.get(),
                        SoundSource.BLOCKS,
                        1.0f,
                        0.95f+level.random.nextFloat()*0.1f);
            }
        } else if(phase == GoonChamberPhase.DISCHARGING){
            // bleed off charge so it looks cool. charge bleed is purely visual.
            // actual phase transition is time gated below.
            if(currentCharge > 0){
                currentCharge-=DISCHARGE_CHARGE_DECAY;
                if(currentCharge < 0) currentCharge = 0;
            }
            if(timeSinceTickChange() >= DISCHARGE_TIME){
                changePhase(GoonChamberPhase.COOLDOWN);
                //System.out.println("cycle complete??");
                createOutputs();

            }
        } else if(phase == GoonChamberPhase.COOLDOWN){
            // arriving here with charge means the cycle failed earlier
            // so bleed it off at an increased rate that feels sad :(
            if(currentCharge > 0){
                currentCharge-=FAILURE_CHARGE_DECAY;
                if(currentCharge < 0) currentCharge = 0;
            }

            // cooldown chamber and reset
            if(timeSinceTickChange() >= COOLDOWN_TIME){
                //createOutputs();
                changePhase(GoonChamberPhase.IDLE);
            }
        }
    }

    private void changePhase(GoonChamberPhase newPhase){
        this.phase = newPhase;
        lastPhaseChangeTick = level.getGameTime();
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        //System.out.println("changing phase - new phase: " + newPhase.name());
    }

    private long timeSinceTickChange(){
        return level.getGameTime() - lastPhaseChangeTick;
    }

    protected void startTickHook(BlockPos pos, BlockState state) {
        changePhase(GoonChamberPhase.CHARGING);
    }

    private void startTickClient(BlockPos pos, BlockState state){
        if(level.isClientSide ){
            GoonChamberSound.start(level, pos);
        }
    }

    protected void saveExtra(CompoundTag tag) {
        tag.putInt("Phase", phase.getIndex());
        tag.putInt("Charge", currentCharge);
        tag.putInt("MaxCharge", maxCharge);
        tag.put("Tank0", fluidHandler.getFluidInTank(0).writeToNBT(new CompoundTag()));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setMultiblockDirty();

        for(BlockPos p: blocks){
            if(level.getBlockEntity(p) instanceof ChamberPartBlockEntity part){
                part.makeChamber(getBlockPos());
            }

            if(level.getBlockState(p).getBlock() instanceof HyperbolicGoonChamberPartBlock part){
                part.form(level, getBlockPos(), getBlockState());
            }
        }
    }

    protected void loadExtra(CompoundTag tag) {
        int phaseIndex = tag.getInt("Phase");
        GoonChamberPhase newPhase = GoonChamberPhase.IDLE;
        switch (phaseIndex) {
            case 1 -> newPhase = GoonChamberPhase.CHARGING;
            case 2 -> newPhase = GoonChamberPhase.DISCHARGING;
            case 3 -> newPhase = GoonChamberPhase.COOLDOWN;
        }
        phase = newPhase;
        currentCharge = tag.getInt("Charge");
        maxCharge = tag.getInt("MaxCharge");
        if(tag.contains("Tank0")) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(tag.getCompound("Tank0"));
            fluidHandler.setFluid(stack);
        }
    }


    public GoonEnergyStorage getEnergyStorage(){
        return this.energyStorage;
    }

    public GoonFluidHandler getFluidHandler(){
        return this.fluidHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.gooninite.hyperbolic_goon_chamber");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new HyperbolicGoonChamberMenu(pContainerId, pPlayerInventory, this, data);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public GoonChamberPhase getPhase() {
        return phase;
    }

    public int getEnergy(){
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergy(){
        return energyStorage.getMaxEnergyStored();
    }

    public void cleanupMultiblockStates() {
        for(BlockPos p: List.copyOf(blocks)){
            BlockState state = level.getBlockState(p);
            BlockEntity be = level.getBlockEntity(p);

            if(state.getBlock() instanceof HyperbolicGoonChamberPartBlock block){
                block.unform(level, p, state);
            }
            if(be instanceof ChamberPartBlockEntity part){
                part.breakChamber();
            }
        }
    }

    public void setMultiblockDirty(){
        this.needsValidated=true;
    }


    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.put("Items", itemHandler.serializeNBT());

        saveExtra(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        itemHandler.deserializeNBT(tag.getCompound("Items"));
        energyStorage.setEnergy(tag.getInt("Energy"));

        loadExtra(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
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
}
