package net.cpeek.gooninite;


import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GooniniteSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Gooninite.MODID);

    /*public static final RegistryObject<SoundEvent> GOON_CHAMBER_CYCLE_TEST =
            SOUNDS.register("machine.hgc_cycle",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(Gooninite.MODID, "machine.hgc_cycle")
                    ));*/

    public static final RegistryObject<SoundEvent> GOON_CHAMBER_SPIN_UP =
            SOUNDS.register("machine.hgc_spinup",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(Gooninite.MODID, "machine.hgc_spinup")
                    ));

    public static final RegistryObject<SoundEvent> GOON_CHAMBER_SPIN_DOWN =
            SOUNDS.register("machine.hgc_spindown",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(Gooninite.MODID, "machine.hgc_spindown")
                    ));

    public static final RegistryObject<SoundEvent> GOON_CHAMBER_DISCHARGE =
            SOUNDS.register("machine.hgc_discharge",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(Gooninite.MODID, "machine.hgc_discharge")
                    ));

}
