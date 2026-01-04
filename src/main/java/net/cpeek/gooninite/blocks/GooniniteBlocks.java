package net.cpeek.gooninite.blocks;

import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.blocks.machines.MechanicalSinteringPressBlock;
import net.cpeek.gooninite.blocks.machines.PressPowerPortBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.cpeek.gooninite.blocks.GooniniteFluids.GOON_JUICE;

public class GooniniteBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Gooninite.MODID);

    // Gooninite Drip Block (the stalactite looking mf)
    public static final RegistryObject<Block> GOONINITE_DRIP = BLOCKS.register("gooninite_drip", ()-> new GooniniteDripBlock(
            BlockBehaviour.Properties.of()
                    .strength(1.0f, 1.0f)
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
    );

    // Solid Gooninite Drip Block
    public static final RegistryObject<Block> GOONINITE_DRIP_BLOCK = BLOCKS.register("gooninite_drip_block", () -> new DropExperienceBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops(),
            UniformInt.of(1,4)  // XP range the block drops
    ));

    // Gooninite Ore
    public static final RegistryObject<Block> GOONINITE_ORE = BLOCKS.register("gooninite_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops(),
                    UniformInt.of(1,4)  // XP range
            ));

    // Goon Juice Source Block
    public static final RegistryObject<Block> GOON_JUICE_BLOCK = BLOCKS.register( "goon_juice",
            () -> new LiquidBlock(GOON_JUICE, BlockBehaviour.Properties.copy(Blocks.WATER))
    );

    public static final RegistryObject<Block> HYPERBOLIC_GOON_CHAMBER = BLOCKS.register("hyperbolic_goon_chamber",
            () -> new HyperbolicGoonChamberBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> MECHANICAL_SINTER_PRESS = BLOCKS.register("mechanical_sinter_press",
            () -> new MechanicalSinteringPressBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .noOcclusion()));

    public static final RegistryObject<Block> PRESS_RAM = BLOCKS.register("mechanical_sinter_press_ram",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> PRESS_PORT = BLOCKS.register("press_port",
            () -> new PressPowerPortBlock(BlockBehaviour.Properties.of()
                    .noParticlesOnBreak()));

    public static void register(IEventBus bus){
        BLOCKS.register(bus);
    }
}
