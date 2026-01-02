package net.cpeek.gooninite.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalSinteringPressBE extends BlockEntity {
    private float progress;
    private float clientProgress;

    private PressPhase phase = PressPhase.IDLE;

    public MechanicalSinteringPressBE(BlockPos pos, BlockState state) {
        super(GooniniteBlockEntities.PRESS_BE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MechanicalSinteringPressBE be){
        float speed = 0.05f;
        be.progress += speed;
        if(be.progress > 1f) be.progress = 0f;

        be.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    public float getProgress(float partialTick){
        if(level != null && level.isClientSide){
            clientProgress = clientProgress + (progress-clientProgress)*0.25f;
            return clientProgress;
        }
        return progress;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("Progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getFloat("Progress");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("Progress", progress);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        progress = tag.getFloat("Progress");
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
