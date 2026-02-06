package net.cpeek.gooninite.blocks.machines.hgc;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GoonPowerPortBlock extends GoonCasingBlock implements EntityBlock {
    public GoonPowerPortBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GoonPowerPortBE(pPos, pState);
    }
}
