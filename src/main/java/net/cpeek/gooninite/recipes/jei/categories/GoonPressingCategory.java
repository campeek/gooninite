package net.cpeek.gooninite.recipes.jei.categories;


import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.recipes.GoonPressingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GoonPressingCategory implements IRecipeCategory<GoonPressingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Gooninite.MODID, "goon_pressing");

    public static final RecipeType<GoonPressingRecipe> RECIPE_TYPE =
            new RecipeType<>(UID, GoonPressingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public GoonPressingCategory(IGuiHelper helper){
        this.background = helper.createBlankDrawable(150, 60);
        this.icon = helper.createDrawableItemStack(
                new ItemStack(GooniniteBlocks.MECHANICAL_SINTER_PRESS.get())
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GoonPressingRecipe recipe, IFocusGroup focuses) {

        // TODO: make this look like not-dogshit
        // Inputs
        builder.addSlot(RecipeIngredientRole.INPUT,
                20, 20).addIngredients(recipe.getInputItem());

        // Output
        builder.addSlot(RecipeIngredientRole.OUTPUT,
                100, 10)
                .addItemStack(recipe.getOutputItem());
    }

    @Override
    public RecipeType<GoonPressingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.gooninite.mechanical_press");
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }
}
