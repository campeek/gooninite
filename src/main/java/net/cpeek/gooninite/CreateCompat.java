package net.cpeek.gooninite;

import com.simibubi.create.api.stress.BlockStressValues;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod.EventBusSubscriber(modid = "gooninite", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateCompat {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
            BlockStressValues.IMPACTS.register(GooniniteBlocks.PRESS_PORT.get(), () -> 16.0);
        });
    }
}
