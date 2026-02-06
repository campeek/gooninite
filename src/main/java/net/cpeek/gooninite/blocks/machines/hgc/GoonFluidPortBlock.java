package net.cpeek.gooninite.blocks.machines.hgc;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GoonFluidPortBlock extends GoonCasingBlock implements EntityBlock {
    public GoonFluidPortBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GoonFluidPortBE(pos, state);
    }
}
