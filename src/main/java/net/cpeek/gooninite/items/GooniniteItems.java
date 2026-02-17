package net.cpeek.gooninite.items;

import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.GooniniteArmorMaterials;
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
    public static final RegistryObject<Item> GOONINITE_DRIP_ITEM = ITEMS.register("gooninite_drip", () -> new BlockItem(GOONINITE_DRIP.get(), new Item.Properties()));
    //public static final RegistryObject<Item> GOONINITE_DRIP_BLOCK_ITEM = ITEMS.register("gooninite_drip_block", () -> new BlockItem(GOONINITE_DRIP_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> PHASE_DESTABILIZER_ITEM = ITEMS.register("phase_destabilizer", () ->new BlockItem(PHASE_DESTABILIZER.get(), new Item.Properties()));
    public static final RegistryObject<Item> LATTICE_RECRYSTALLIZER_ITEM = ITEMS.register("lattice_recrystallizer", () -> new BlockItem(LATTICE_RECRYSTALLIZER.get(), new Item.Properties()));
    public static final RegistryObject<Item> MECHANICAL_SINTER_PRESS_ITEM = ITEMS.register("mechanical_sinter_press", () -> new BlockItem(MECHANICAL_SINTER_PRESS.get(), new Item.Properties()));

    public static final RegistryObject<Item> HYPERBOLIC_GOON_CHAMBER_CORE_ITEM = ITEMS.register("hyperbolic_goon_chamber_core", () -> new BlockItem(HYPERBOLIC_GOON_CHAMBER_CORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> HYPERBOLIC_GOON_CHAMBER_CONTROLLER_ITEM = ITEMS.register("hyperbolic_goon_chamber_controller", () -> new BlockItem(HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOON_FLUID_PORT_ITEM = ITEMS.register("goon_fluid_port", () -> new BlockItem(GOON_FLUID_PORT.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOON_POWER_PORT_ITEM = ITEMS.register("goon_power_port", () -> new BlockItem(GOON_POWER_PORT.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOON_CHAMBER_CASING_ITEM = ITEMS.register("goon_chamber_casing", () -> new BlockItem(GOON_CHAMBER_CASING.get(), new Item.Properties()));
    public static final RegistryObject<Item> GOON_CAPACITOR_ITEM = ITEMS.register("goon_capacitor", () -> new BlockItem(GOON_CAPACITOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> CHAMBER_FRAME_ITEM = ITEMS.register("chamber_frame", () -> new BlockItem(CHAMBER_FRAME.get(), new Item.Properties()));


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
                    .durability(3)
                    .setNoRepair()
    ));

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
            new Item.Properties()
    ));
    public static final RegistryObject<Item> GOONINITE_AXE = ITEMS.register("gooninite_axe", () -> new AxeItem(
            GooniniteTiers.GOONINITE,
            4f,
            -3.0f,
            new Item.Properties()
    ));
    public static final RegistryObject<Item> GOONINITE_SHOVEL = ITEMS.register("gooninite_shovel", () -> new ShovelItem(
            GooniniteTiers.GOONINITE,
            1.5f,
            -3.0f,
            new Item.Properties()
    ));
    public static final RegistryObject<Item> GOONINITE_HOE = ITEMS.register("gooninite_hoe", () -> new HoeItem(
            GooniniteTiers.GOONINITE,
            -4,
            0.0f,
            new Item.Properties()
    ));
    public static final RegistryObject<Item> GOONINITE_SWORD = ITEMS.register("gooninite_sword", () -> new SwordItem(
            GooniniteTiers.GOONINITE,
            3,
            -2.4f,
            new Item.Properties()
    ));

    // Armor
    public static final RegistryObject<Item> GOON_HELMET = ITEMS.register(
            "gooninite_helmet",
            () -> new ArmorItem(GooniniteArmorMaterials.GOON_ARMOR, ArmorItem.Type.HELMET, new Item.Properties())
    );
    public static final RegistryObject<Item> GOON_CHESTPLATE = ITEMS.register(
            "gooninite_chestplate",
            () -> new ArmorItem(GooniniteArmorMaterials.GOON_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    public static final RegistryObject<Item> GOON_LEGGINGS = ITEMS.register(
            "gooninite_leggings",
            () -> new ArmorItem(GooniniteArmorMaterials.GOON_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    public static final RegistryObject<Item> GOON_BOOTS = ITEMS.register(
            "gooninite_boots",
            () -> new ArmorItem(GooniniteArmorMaterials.GOON_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    public static final RegistryObject<Item> GOON_SMITHING_TEMPLATE = ITEMS.register("goon_smithing_template",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
