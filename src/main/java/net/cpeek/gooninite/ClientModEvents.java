package net.cpeek.gooninite;

import net.cpeek.gooninite.blocks.GooniniteBlockEntities;
import net.cpeek.gooninite.blocks.machines.mech_press.MechanicalSinteringPressBER;
import net.cpeek.gooninite.menus.*;
import net.cpeek.gooninite.particles.GoonJuiceDrippingParticle;
import net.cpeek.gooninite.particles.GoonJuiceFallingParticle;
import net.cpeek.gooninite.particles.GoonJuiceSquirtParticle;
import net.cpeek.gooninite.particles.GoonParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = Gooninite.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(GoonParticles.GOON_JUICE_FALLING.get(), GoonJuiceFallingParticle.Provider::new);
        event.registerSpriteSet(GoonParticles.GOON_JUICE_DRIPPING.get(), GoonJuiceDrippingParticle.Provider::new);
        event.registerSpriteSet(GoonParticles.GOON_JUICE_SQUIRT.get(), GoonJuiceSquirtParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(GooniniteMenus.PHASE_DESTABILIZER_MENU.get(), PhaseDestabilizerScreen::new);
            MenuScreens.register(GooniniteMenus.MECHANICAL_PRESS_MENU.get(), MechanicalPressScreen::new);
            MenuScreens.register(GooniniteMenus.LATTICE_RECRYSTALLIZER_MENU.get(), LatticeRecrystallizerScreen::new);
            MenuScreens.register(GooniniteMenus.HYPERBOLIC_GOON_CHAMBER_MENU.get(), HyperbolicGoonChamberScreen::new);

            BlockEntityRenderers.register(GooniniteBlockEntities.PRESS_BE.get(), MechanicalSinteringPressBER::new);
        });

        var rm = Minecraft.getInstance().getResourceManager();
        var resId = new ResourceLocation(Gooninite.MODID, "block/machines/mechanical_sinter_press_ram");

        rm.getResource(resId).ifPresentOrElse(
                r-> System.out.println("FOUND RAM RESOURCE " + r.sourcePackId()),
                () -> System.out.println("MISSING RAM RESOURCE")
        );


        var staticId = new ResourceLocation(Gooninite.MODID, "models/block/machines/mechanical_sinter_press_static.json");
        rm.getResource(staticId).ifPresentOrElse(
                r-> System.out.println("FOUND STATIC RESOURCE " + r.sourcePackId()),
                () -> System.out.println("MISSING STATIC RESOURCE")
        );
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event){
        event.register(new ResourceLocation(Gooninite.MODID, "block/mechanical_sinter_press_ram"));
    }
}
