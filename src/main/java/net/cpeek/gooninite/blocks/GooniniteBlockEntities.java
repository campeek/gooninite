package net.cpeek.gooninite.blocks;


import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.blocks.machines.MechanicalSinteringPressBE;
import net.cpeek.gooninite.blocks.machines.PressPowerPortBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GooniniteBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Gooninite.MODID);

    public static final RegistryObject<BlockEntityType<PhaseDestabilizerBlockEntity>> PHASE_DESTABILIZER = BLOCK_ENTITIES.register(
            "phase_destabilizer",
            ()-> BlockEntityType.Builder.of(
                    PhaseDestabilizerBlockEntity::new,
                    GooniniteBlocks.PHASE_DESTABILIZER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<LatticeRecrystallizerBlockEntity>> LATTICE_RECRYSTALLIZER = BLOCK_ENTITIES.register(
            "lattice_recrystallizer",
            () -> BlockEntityType.Builder.of(
                    LatticeRecrystallizerBlockEntity::new,
                    GooniniteBlocks.LATTICE_RECRYSTALLIZER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<MechanicalSinteringPressBE>> PRESS_BE = BLOCK_ENTITIES.register(
            "mechanical_press",
            () -> BlockEntityType.Builder.of(
                    MechanicalSinteringPressBE::new,
                    GooniniteBlocks.MECHANICAL_SINTER_PRESS.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<PressPowerPortBE>> PRESS_PORT_BE = BLOCK_ENTITIES.register(
            "press_port",
            () -> BlockEntityType.Builder.of(
                    PressPowerPortBE::new,
                    GooniniteBlocks.PRESS_PORT.get()
            ).build(null));

    public static void register(IEventBus bus){

        BLOCK_ENTITIES.register(bus);
    }
}
