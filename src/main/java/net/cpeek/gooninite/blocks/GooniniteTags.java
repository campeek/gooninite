package net.cpeek.gooninite.blocks;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class GooniniteTags {
    public static class Blocks{
        public static final TagKey<Block> GOON_CHAMBER_BLOCKS =
                TagKey.create(Registries.BLOCK, new ResourceLocation("gooninite", "goon_chamber_block"));
        public static final TagKey<Block> GOON_TILEABLE_BLOCKS =
                TagKey.create(Registries.BLOCK, new ResourceLocation("gooninite", "goon_tileable_block"));
    }
}
