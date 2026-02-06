package net.cpeek.gooninite.blocks;


import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.blocks.machines.hgc.GoonFluidPortBE;
import net.cpeek.gooninite.blocks.machines.hgc.GoonPowerPortBE;
import net.cpeek.gooninite.blocks.machines.lattice_recrystallizer.LatticeRecrystallizerBlockEntity;
import net.cpeek.gooninite.blocks.machines.mech_press.MechanicalSinteringPressBE;
import net.cpeek.gooninite.blocks.machines.mech_press.PressPowerPortBE;
import net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberControllerBE;
import net.cpeek.gooninite.blocks.machines.phase_destabilizer.PhaseDestabilizerBlockEntity;
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

    public static final RegistryObject<BlockEntityType<HyperbolicGoonChamberControllerBE>> HYPERBOLIC_GOON_CHAMBER_CONTROLLER = BLOCK_ENTITIES.register(
            "hyperbolic_goon_chamber_controller",
            () -> BlockEntityType.Builder.of(
                    HyperbolicGoonChamberControllerBE::new,
                    GooniniteBlocks.HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<GoonFluidPortBE>> GOON_FLUID_PORT_BE = BLOCK_ENTITIES.register(
            "goon_fluid_port",
            () -> BlockEntityType.Builder.of(
                    GoonFluidPortBE::new,
                    GooniniteBlocks.GOON_FLUID_PORT.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<GoonPowerPortBE>> GOON_POWER_PORT_BE = BLOCK_ENTITIES.register(
            "goon_power_port",
            () -> BlockEntityType.Builder.of(
                    GoonPowerPortBE::new,
                    GooniniteBlocks.GOON_POWER_PORT.get()
            ).build(null));


    public static void register(IEventBus bus){

        BLOCK_ENTITIES.register(bus);
    }
}
