package net.cpeek.gooninite.blocks.machines;


import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MechanicalSinteringPressBE extends BlockEntity {

    private float progress = 0; // how far along the process is

    private boolean running;
    private long cycleStartTick;
    private long lastPhaseChange;

    private final int pistonTime  = 3;        // piston extension/retraction time in seconds
    private final int dwellTime   = 8;         // piston dwell time in seconds

    private final int tickrate = 20;

    private final int totalTime = pistonTime*2+dwellTime;
    private final float pistonMovementRatio = (float)pistonTime/totalTime;
    private final float dwellRatio = (float)dwellTime/totalTime;

    public final int totalTicks = totalTime*tickrate;
    public final int pistonMoveTicks = (int) ((totalTime*tickrate)*pistonMovementRatio);
    public final int pistonDwellTicks = (int) ((totalTime*tickrate)*dwellRatio);

    private final float pistonSpeed = 1f/(pistonTime*tickrate);

    private PressPhase phase = PressPhase.IDLE;

    private float RPM = 0f;

    public MechanicalSinteringPressBE(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.PRESS_BE.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MechanicalSinteringPressBE be){
        // increase progress at a constant rate
        be.progress += 1f/(be.totalTime*be.tickrate);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MechanicalSinteringPressBE be){
        /*float speed = 0.05f;
        be.progress += speed;
        if(be.progress > 1f) be.progress = 0f;

        be.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
         */

        if(!be.running && be.shouldStartCycle()){                       // shouldStartCycle() checks everything to see if we should cycle (duh)
            be.running = true;
            be.phase = PressPhase.DESCEND;
            be.progress = 0;
            be.cycleStartTick = level.getGameTime();
            be.lastPhaseChange = be.cycleStartTick;
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);

        }

        if(be.running){
            long age = level.getGameTime() - be.cycleStartTick;
            long phaseTicks = level.getGameTime() - be.lastPhaseChange;


            //System.out.println(be.progress);
            if(phaseTicks >= be.pistonMoveTicks && be.phase == PressPhase.DESCEND){
                System.out.println("Phase lasts " + be.pistonDwellTicks + " ticks");
                be.phase = PressPhase.PRESS;
                be.lastPhaseChange = level.getGameTime();
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            } else if (phaseTicks >= be.pistonDwellTicks && be.phase == PressPhase.PRESS){
                System.out.println("Phase lasts " + be.pistonMoveTicks + " ticks");
                be.phase = PressPhase.ASCEND;
                be.lastPhaseChange = level.getGameTime();
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            } else if(phaseTicks >= be.pistonMoveTicks && be.phase == PressPhase.ASCEND){
                be.phase = PressPhase.IDLE;
                be.lastPhaseChange = level.getGameTime();
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                be.running = false;
            }
        }
    }

    private boolean shouldStartCycle(){
        return true;
    }

    private float clamp(float f, float max, float min){
        if(f>max) return max;
        if(f<min) return min;
        return f;
    }

    public float getProgress(){
        return progress;
    }

    public PressPhase getPhase(){
        return phase;
    }

    public void setRPM(float rpm){
        this.RPM = rpm;
    }

    public long getTicksSinceLast(){
        if(level == null) return 0;
        return level.getGameTime() - lastPhaseChange;
    }
    public long getCycleStartTick(){
        if(level == null) return 0;
        return cycleStartTick;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("Progress", progress);
        tag.putBoolean("Running", running);
        tag.putLong("CycleStart", cycleStartTick);
        tag.putLong("LastPhase", lastPhaseChange);
        tag.putString("Phase", phase.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getFloat("Progress");
        running = tag.getBoolean("Running");
        cycleStartTick = tag.getLong("CycleStart");
        lastPhaseChange = tag.getLong("LastPhase");
        phase = PressPhase.valueOf(tag.getString("Phase"));
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("Progress", progress);
        tag.putBoolean("Running", running);
        tag.putLong("CycleStart", cycleStartTick);
        tag.putLong("LastPhase", lastPhaseChange);
        tag.putString("Phase", phase.name());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        progress = tag.getFloat("Progress");
        running = tag.getBoolean("Running");
        cycleStartTick = tag.getLong("CycleStart");
        lastPhaseChange = tag.getLong("LastPhase");
        phase = PressPhase.valueOf(tag.getString("Phase"));

    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection con, ClientboundBlockEntityDataPacket packet){
        load(packet.getTag());
    }
}
