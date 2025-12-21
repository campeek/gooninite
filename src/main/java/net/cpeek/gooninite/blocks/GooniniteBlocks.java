package net.cpeek.gooninite.blocks;

import net.cpeek.gooninite.Gooninite;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// GooniniteBlocks.java
// Cameron Peek
// 12/21/2025
public class GooniniteBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Gooninite.MODID);

    public static final RegistryObject<Block> GOONINITE_DRIP = BLOCKS.register("gooninite_drip", ()-> new GooniniteDripBlock(
            BlockBehaviour.Properties.of()
                    .strength(1.0f, 1.0f)
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
    );

    public static final RegistryObject<Block> GOONINITE_DRIP_BLOCK = BLOCKS.register("gooninite_drip_block", () -> new DropExperienceBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops(),
            UniformInt.of(1,4)  // XP range the block drops
    ));

    public static final RegistryObject<Block> GOONINITE_ORE = BLOCKS.register("gooninite_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops(),
                    UniformInt.of(1,4)  // XP range
            ));

    public static void register(IEventBus bus){
        BLOCKS.register(bus);
    }
}
