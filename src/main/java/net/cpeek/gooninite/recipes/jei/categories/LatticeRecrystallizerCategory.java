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
import net.cpeek.gooninite.recipes.LatticeRecrystallizingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LatticeRecrystallizerCategory implements IRecipeCategory<LatticeRecrystallizingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Gooninite.MODID, "lattice_recrystallizer");

    public static final RecipeType<LatticeRecrystallizingRecipe> RECIPE_TYPE =
            new RecipeType<>(UID, LatticeRecrystallizingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public LatticeRecrystallizerCategory(IGuiHelper guiHelper){
        this.background = guiHelper.createBlankDrawable(150,60);
        this.icon = guiHelper.createDrawableItemStack(
                new ItemStack(GooniniteBlocks.LATTICE_RECRYSTALLIZER.get())
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LatticeRecrystallizingRecipe recipe, IFocusGroup focuses) {

        // TODO: make this look like not-dogshit
        // Inputs
        builder.addSlot(RecipeIngredientRole.INPUT,
                20, 20).addIngredients(recipe.ingredient());


        builder.addSlot(RecipeIngredientRole.INPUT, 50, 10)
                        .addFluidStack(recipe.ingredientFluid().getFluid())
                        .setFluidRenderer(
                                recipe.ingredientFluid().getAmount(),
                                false,
                                16,
                                45);



        // Outputs
        builder.addSlot(RecipeIngredientRole.OUTPUT,
                100, 10)
                .addItemStack(recipe.getOutputItem());

    }

    @Override
    public RecipeType<LatticeRecrystallizingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.gooninite.lattice_recrystallizer");
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
