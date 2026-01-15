package net.cpeek.gooninite.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class LatticeRecrystallizerBlock extends GooniniteMachineBlock {

    public LatticeRecrystallizerBlock(Properties pProperties) {
        super(pProperties, LatticeRecrystallizerBlockEntity::new);

    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof LatticeRecrystallizerBlockEntity be &&
        player instanceof ServerPlayer serverPlayer){
            NetworkHooks.openScreen(serverPlayer, be, pos);
            return InteractionResult.CONSUME;
        } else {
            System.out.println(entity.getClass());
        }
        return InteractionResult.PASS;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        return !level.isClientSide
                ? createTicker(type, GooniniteBlockEntities.LATTICE_RECRYSTALLIZER.get(), LatticeRecrystallizerBlockEntity::serverTick)
                : null;
    }

    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
            BlockEntityType<T> type,
            BlockEntityType<? extends BlockEntity> expectedType,
            BlockEntityTicker<? extends LatticeRecrystallizerBlockEntity> ticker) {
        return type == expectedType? (BlockEntityTicker<T>) ticker : null;
    }

}
