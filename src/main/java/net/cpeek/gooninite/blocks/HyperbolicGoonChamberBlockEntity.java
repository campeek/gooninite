package net.cpeek.gooninite.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HyperbolicGoonChamberBlockEntity extends BlockEntity {
    public HyperbolicGoonChamberBlockEntity(BlockPos pos, BlockState state){
        super(GooniniteBlockEntities.HYPERBOLIC_GOON_CHAMBER.get(), pos, state);
    }

    private final EnergyStorage energy = new EnergyStorage(
            100000,     // capacity
            5000,               // max receive
            0                   // max extract (0=input only)
    );

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(
        @NotNull Capability<T> cap,
        @Nullable Direction side
    ){
        if(cap == ForgeCapabilities.ENERGY      // requesting energy capabilities
                && side == getBlockState().getValue(HyperbolicGoonChamberBlock.FACING).getOpposite()){ // where the requested capability is allowed to hook up
            return energyCap.cast();                                                                   // opposite of the direction the block is facing = the back
        }
        return super.getCapability(cap, side);  // otherwise pass it on
    }
}
