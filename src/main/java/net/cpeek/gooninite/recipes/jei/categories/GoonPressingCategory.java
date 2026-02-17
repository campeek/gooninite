package net.cpeek.gooninite.recipes.jei.categories;


import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.recipes.BaseGoonRecipe;
import net.cpeek.gooninite.recipes.GoonPressingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GoonPressingCategory extends GoonBaseCategory {
    public static final ResourceLocation UID = new ResourceLocation(Gooninite.MODID, "goon_pressing");

    public static final RecipeType<GoonPressingRecipe> RECIPE_TYPE =
            new RecipeType<>(UID, GoonPressingRecipe.class);

    public GoonPressingCategory(IGuiHelper guiHelper) {
        super(guiHelper, GooniniteBlocks.MECHANICAL_SINTER_PRESS.get());
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BaseGoonRecipe recipe, IFocusGroup focuses) {
        if(recipe instanceof GoonPressingRecipe pressing) {
            builder.addSlot(RecipeIngredientRole.INPUT,
                    76, 24).addIngredients(recipe.ingredient());

            // Output
            builder.addSlot(RecipeIngredientRole.OUTPUT,
                            121, 24)
                    .addItemStack(pressing.getOutputItem());
        }
    }

    @Override
    protected void setBackground(IGuiHelper helper) {
        IDrawableBuilder builder = helper.drawableBuilder(new ResourceLocation(Gooninite.MODID, "textures/gui/jei/mech_press.png"),
                0, 0,
                164, 60);
        builder.setTextureSize(164, 60);
        this.background = builder.build();
    }

    @Override
    public RecipeType getRecipeType() {
        return RECIPE_TYPE;
    }


    @Override
    public Component getTitle() {
        return Component.translatable("block.gooninite.mechanical_press");
    }
}
