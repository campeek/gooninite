package net.cpeek.gooninite.recipes;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GooniniteRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Gooninite.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Gooninite.MODID);

    public static final RegistryObject<RecipeSerializer<GoonPressingRecipe>> PRESSING_SERIALIZER = SERIALIZERS.register("goon_pressing", PressingRecipeSerializer::new);
    public static final RegistryObject<RecipeType<GoonPressingRecipe>> GOON_PRESSING_RECIPE = TYPES.register("goon_pressing", () -> new RecipeType<>() {});

    public static void register(IEventBus bus){
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
