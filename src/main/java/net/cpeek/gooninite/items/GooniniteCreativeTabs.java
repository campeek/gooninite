package net.cpeek.gooninite.items;

import net.cpeek.gooninite.Gooninite;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class GooniniteCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Gooninite.MODID);

    public static final RegistryObject<CreativeModeTab> GOONINITE_TAB = TABS.register("gooninite_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("Gooninite"))
            .icon(() -> new ItemStack(GooniniteItems.GOONINITE_PICKAXE.get()))
            .displayItems((params, output) -> {
                output.accept(GooniniteItems.PHASE_DESTABILIZER_ITEM.get());
                output.accept(GooniniteItems.GOONINITE_PICKAXE.get());
                output.accept(GooniniteItems.GOONINITE_INGOT.get());
                output.accept(GooniniteItems.GOONINITE_NUGGET_ITEM.get());
                output.accept(GooniniteItems.GOONINITE_PELLET_ITEM.get());
                output.accept(GooniniteItems.GOONINITE_DRIP_ITEM.get());
                output.accept(GooniniteItems.GOONINITE_DRIP_BLOCK_ITEM.get());
                output.accept(GooniniteItems.GOON_JUICE_BUCKET.get());
                output.accept(GooniniteItems.MECHANICAL_SINTER_PRESS_ITEM.get());
            }).build());

    public static void register(IEventBus bus){
        TABS.register(bus);
    }

    @SubscribeEvent
    public static void addToTabs(BuildCreativeModeTabContentsEvent event){

    }
}
