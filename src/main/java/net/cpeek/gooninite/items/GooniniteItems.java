package net.cpeek.gooninite.items;

import net.cpeek.gooninite.Gooninite;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.cpeek.gooninite.blocks.GooniniteBlocks.*;
import static net.cpeek.gooninite.fluids.GooniniteFluids.GOON_JUICE;

public class GooniniteItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Gooninite.MODID);

    // Block Items
    public static final RegistryObject<Item> GOONINITE_ORE_ITEM = ITEMS.register("gooninite_ore", () -> new BlockItem(GOONINITE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOONINITE_DRIP_ITEM = ITEMS.register("gooninite_drip", () -> new BlockItem(GOONINITE_DRIP.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOONINITE_DRIP_BLOCK_ITEM = ITEMS.register("gooninite_drip_block", () -> new BlockItem(GOONINITE_DRIP_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> PHASE_DESTABILIZER_ITEM = ITEMS.register("phase_destabilizer", () ->new BlockItem(PHASE_DESTABILIZER.get(), new Item.Properties()));
    public static final RegistryObject<Item> LATTICE_RECRYSTALLIZER_ITEM = ITEMS.register("lattice_recrystallizer", () -> new BlockItem(LATTICE_RECRYSTALLIZER.get(), new Item.Properties()));
    public static final RegistryObject<Item> MECHANICAL_SINTER_PRESS_ITEM = ITEMS.register("mechanical_sinter_press", () -> new BlockItem(MECHANICAL_SINTER_PRESS.get(), new Item.Properties()));

    // Normal Items
    public static final RegistryObject<Item> GOONINITE_NUGGET_ITEM = ITEMS.register("gooninite_nugget", () -> new Item(
            new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .stacksTo(16)));
    public static final RegistryObject<Item> GOONINITE_PELLET_ITEM = ITEMS.register("gooninite_pellet", () -> new Item(
            new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .stacksTo(16)
    ));

    public static final RegistryObject<Item> GOONINITE_LINER_ITEM = ITEMS.register("gooninite_liner", () -> new Item(
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .durability(64)
    ));

    public static final RegistryObject<Item> RAW_GOONINITE = ITEMS.register("raw_gooninite", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));
    public static final RegistryObject<Item> GOONINITE_INGOT = ITEMS.register("gooninite_ingot", () -> new Item(new Item.Properties().fireResistant()));

    // Fluid Items
    public static final RegistryObject<Item> GOON_JUICE_BUCKET = ITEMS.register("goon_juice_bucket", () -> new BucketItem(GOON_JUICE, new Item.Properties()
            .stacksTo(1)
            .craftRemainder(Items.BUCKET))
    );



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
