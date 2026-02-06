package net.cpeek.gooninite.blocks.machines.phase_destabilizer;

import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.machines.GooniniteMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class PhaseDestabilizerBlock extends GooniniteMachineBlock implements EntityBlock {

    public PhaseDestabilizerBlock(Properties props){
        super(props, PhaseDestabilizerBlockEntity::new);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof PhaseDestabilizerBlockEntity chamberBlockEntity &&
                player instanceof ServerPlayer sp){
            NetworkHooks.openScreen(sp, chamberBlockEntity, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        return !level.isClientSide
                ? createTicker(type, GooniniteBlockEntities.PHASE_DESTABILIZER.get(), PhaseDestabilizerBlockEntity::serverTick)
                : null;
    }

    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
            BlockEntityType<T> type,
            BlockEntityType<? extends BlockEntity> expectedType,
            BlockEntityTicker<? extends PhaseDestabilizerBlockEntity> ticker) {
        return type == expectedType? (BlockEntityTicker<T>) ticker : null;
    }
}
