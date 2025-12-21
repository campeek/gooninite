package net.cpeek.gooninite.items;

import net.cpeek.gooninite.Gooninite;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.cpeek.gooninite.blocks.GooniniteBlocks.*;

public class GooniniteItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Gooninite.MODID);

    // Block Items
    public static final RegistryObject<Item> GOONINITE_ORE_ITEM = ITEMS.register("gooninite_ore", () -> new BlockItem(GOONINITE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOONINITE_DRIP_ITEM = ITEMS.register("gooninite_drip", () -> new BlockItem(GOONINITE_DRIP.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOONINITE_DRIP_BLOCK_ITEM = ITEMS.register("gooninite_drip_block", () -> new BlockItem(GOONINITE_DRIP_BLOCK.get(), new Item.Properties()));

    // Normal Items
    public static final RegistryObject<Item> GOONINITE_NUGGET_ITEM = ITEMS.register("gooninite_nugget", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> RAW_GOONINITE = ITEMS.register("raw_gooninite", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));
    public static final RegistryObject<Item> GOONINITE_INGOT = ITEMS.register("gooninite_ingot", () -> new Item(new Item.Properties().fireResistant()));


    // Tools
    public static final RegistryObject<Item> GOONINITE_PICKAXE = ITEMS.register("gooninite_pickaxe", () -> new PickaxeItem(
            GooniniteTiers.GOONINITE,   // tool tier
            1,                          // attack damage modifier
            -2.8f,                      // attack speed
            new Item.Properties()));

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
