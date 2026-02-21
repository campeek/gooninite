package net.cpeek.gooninite.sounds;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IGoonChamberAudio {
    void start(Level level, BlockPos pos);
    void stop(BlockPos pos);
}
