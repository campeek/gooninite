package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GooniniteMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Gooninite.MODID);

    public static final RegistryObject<MenuType<HyperbolicGoonChamberMenu>> HYPERBOLIC_GOON_CHAMBER_MENU = MENUS.register("hyperbolic_goon_chamber_menu",
            () -> IForgeMenuType.create(HyperbolicGoonChamberMenu::new));

    public static final RegistryObject<MenuType<MechanicalPressMenu>> MECHANICAL_PRESS_MENU = MENUS.register("mechanical_press_menu",
            () -> IForgeMenuType.create(MechanicalPressMenu::new));

    public static void register(IEventBus bus){
        MENUS.register(bus);
    }
}
