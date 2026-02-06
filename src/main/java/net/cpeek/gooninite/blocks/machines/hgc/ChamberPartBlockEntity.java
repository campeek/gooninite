package net.cpeek.gooninite.blocks.machines.hgc;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberPartBlock.FORMED;

public abstract class ChamberPartBlockEntity extends BlockEntity {
    protected BlockPos controllerPos = null;

    public ChamberPartBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void makeChamber(BlockPos controllerPos){
        this.controllerPos = controllerPos;
        System.out.println(this.getClass() + " formed block");
    }

    public void breakChamber(){
        this.controllerPos = null;
    }


    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(getBlockState().getValue(FORMED))
            tag.putLong("Controller", controllerPos.asLong());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(getBlockState().getValue(FORMED))
            controllerPos = BlockPos.of(tag.getLong("Controller"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    /*@Override
    public void onLoad() {
        super.onLoad();
        handleChangeRecipe();
    }*/

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    /**
     * cleanup resources (invalidate caps etc)
     */
    protected abstract void cleanup();
}
