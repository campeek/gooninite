package net.cpeek.gooninite;

import net.cpeek.gooninite.menus.GooniniteMenus;
import net.cpeek.gooninite.menus.HyperbolicGoonChamberScreen;
import net.cpeek.gooninite.particles.GoonJuiceDrippingParticle;
import net.cpeek.gooninite.particles.GoonJuiceFallingParticle;
import net.cpeek.gooninite.particles.GoonParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Gooninite.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(GoonParticles.GOON_JUICE_FALLING.get(), GoonJuiceFallingParticle.Provider::new);
        event.registerSpriteSet(GoonParticles.GOON_JUICE_DRIPPING.get(), GoonJuiceDrippingParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(GooniniteMenus.HYPERBOLIC_GOON_CHAMBER_MENU.get(), HyperbolicGoonChamberScreen::new);
        });
    }
}
