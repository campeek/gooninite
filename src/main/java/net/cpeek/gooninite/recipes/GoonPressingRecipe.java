package net.cpeek.gooninite.recipes;


import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record GoonPressingRecipe(
        ResourceLocation id,
        Ingredient ingredient,
        ItemStack result,
        int processingTime,
        int minRpm,
        boolean requiresHeat
) implements Recipe<SimpleContainer> {
    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GooniniteRecipes.PRESSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GooniniteRecipes.GOON_PRESSING_RECIPE.get();
    }
}
