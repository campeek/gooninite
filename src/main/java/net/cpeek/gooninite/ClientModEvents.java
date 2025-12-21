package net.cpeek.gooninite;

// ClientModEvents.java
// Cameron Peek
// 12/20/2025

import net.cpeek.gooninite.particles.GoonJuiceDrippingParticle;
import net.cpeek.gooninite.particles.GoonJuiceFallingParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Gooninite.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(GoonParticles.GOON_JUICE_FALLING.get(), GoonJuiceFallingParticle.Provider::new);
        event.registerSpriteSet(GoonParticles.GOON_JUICE_DRIPPING.get(), GoonJuiceDrippingParticle.Provider::new);
    }
}
