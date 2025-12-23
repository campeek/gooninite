package net.cpeek.gooninite.blocks;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GooniniteBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Gooninite.MODID);

    public static final RegistryObject<BlockEntityType<HyperbolicGoonChamberBlockEntity>> HYPERBOLIC_GOON_CHAMBER = BLOCK_ENTITIES.register(
            "hyperbolic_goon_chamber",
            ()-> BlockEntityType.Builder.of(
                    HyperbolicGoonChamberBlockEntity::new,
                    GooniniteBlocks.HYPERBOLIC_GOON_CHAMBER.get()
            ).build(null));

    public static void register(IEventBus bus){
        BLOCK_ENTITIES.register(bus);
    }
}
