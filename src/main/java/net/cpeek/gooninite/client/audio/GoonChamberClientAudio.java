package net.cpeek.gooninite.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GoonChamberClientAudio {
    private static final Map<BlockPos, GoonChamberChargeSound> ACTIVE_SOUNDS = new HashMap<>();

    public static void start(Level level, BlockPos pos){
        if(ACTIVE_SOUNDS.containsKey(pos)) return;

        GoonChamberChargeSound sound = new GoonChamberChargeSound(level, pos);
        ACTIVE_SOUNDS.put(pos, sound);

        Minecraft.getInstance().getSoundManager().play(sound);
    }

    public static void stop(BlockPos pos){
        GoonChamberChargeSound sound = ACTIVE_SOUNDS.remove(pos);
        if(sound != null)
            sound.stopSound();
    }
}
