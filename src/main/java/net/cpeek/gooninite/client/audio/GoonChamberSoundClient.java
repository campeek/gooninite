package net.cpeek.gooninite.client.audio;

import net.cpeek.gooninite.sounds.IGoonChamberAudio;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoonChamberSoundClient implements IGoonChamberAudio {

    @Override
    public void start(Level level, BlockPos pos) {
        GoonChamberClientAudio.start(level, pos);
    }

    @Override
    public void stop(BlockPos pos) {
        GoonChamberClientAudio.stop(pos);
    }
}
