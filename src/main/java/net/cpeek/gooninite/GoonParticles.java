package net.cpeek.gooninite;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// GoonParticles.java
// Cameron Peek
// 12/20/2025
public class GoonParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Gooninite.MODID);

    public static final RegistryObject<SimpleParticleType> GOON_JUICE_FALLING =
            PARTICLES.register("falling_goon_juice", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GOON_JUICE_DRIPPING =
            PARTICLES.register("dripping_goon_juice", () -> new SimpleParticleType(false));

    public static void register(IEventBus bus){
        PARTICLES.register(bus);
    }
}
