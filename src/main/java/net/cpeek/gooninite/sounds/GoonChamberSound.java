package net.cpeek.gooninite.sounds;


import net.cpeek.gooninite.client.audio.GoonChamberSoundClient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;

public final class GoonChamberSound {
    private static final IGoonChamberAudio INSTANCE =
            DistExecutor.safeRunForDist(
                    ()-> GoonChamberSoundClient::new,
                    ()->GoonChamberSoundServer::new
            );

    private GoonChamberSound(){}

    public static void start(Level level, BlockPos pos){
        INSTANCE.start(level, pos);
    }
    public static void stop(BlockPos pos){
        INSTANCE.stop(pos);
    }
}
