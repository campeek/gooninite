package net.cpeek.gooninite.worldgen;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.w3c.dom.ls.LSParserFilter;

public class GooniniteFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Gooninite.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GOON_STALACTITE =
            FEATURES.register("goon_stalactite", () -> new GoonStalactiteFeature(NoneFeatureConfiguration.CODEC));
}
