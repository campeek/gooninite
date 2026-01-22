package net.cpeek.gooninite.fluids;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import static net.cpeek.gooninite.blocks.GooniniteBlocks.GOON_JUICE_BLOCK;
import static net.cpeek.gooninite.items.GooniniteItems.GOON_JUICE_BUCKET;

@SuppressWarnings("removal")
public class GooniniteFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Gooninite.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Gooninite.MODID);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Gooninite.MODID);

    private static final ResourceLocation STILL = new ResourceLocation("minecraft", "block/water_still");
    private static final ResourceLocation FLOW = new ResourceLocation("minecraft", "block/water_flow");


    public static final RegistryObject<FluidType> GOON_JUICE_TYPE = FLUID_TYPES.register("goon_juice", () ->
            new FluidType(FluidType.Properties.create()
                    .density(1100)
                    .viscosity(1500)
                    .canDrown(true)
            ){
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture(){
                            return STILL;
                        }
                        @Override
                        public ResourceLocation getFlowingTexture(){
                            return FLOW;
                        }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> GOON_JUICE = FLUIDS.register("goon_juice", () -> new ForgeFlowingFluid.Source(props()));
    public static final RegistryObject<FlowingFluid> GOON_JUICE_FLOWING = FLUIDS.register("goon_juice_flowing", () -> new ForgeFlowingFluid.Flowing(props()));

    private static ForgeFlowingFluid.Properties props(){
        return new ForgeFlowingFluid.Properties(GOON_JUICE_TYPE, GOON_JUICE, GOON_JUICE_FLOWING)
                .block(() -> (LiquidBlock)GOON_JUICE_BLOCK.get())
                .bucket(GOON_JUICE_BUCKET)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .tickRate(5)
                .explosionResistance(100.0f);
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}
