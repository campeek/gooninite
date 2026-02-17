package net.cpeek.gooninite.recipes.jei.categories;


import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.cpeek.gooninite.recipes.BaseGoonRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class GoonBaseCategory implements IRecipeCategory<BaseGoonRecipe> {

    protected IDrawable background;
    private final IDrawable icon;

    public GoonBaseCategory(IGuiHelper guiHelper, Block machine){
        this.icon = guiHelper.createDrawableItemStack(
                new ItemStack(machine));
        setBackground(guiHelper);
    }


    protected abstract void setBackground(IGuiHelper helper);

    @Override
    public abstract RecipeType getRecipeType();

    @Override
    public abstract Component getTitle();

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public abstract void setRecipe(IRecipeLayoutBuilder builder, BaseGoonRecipe recipe, IFocusGroup focuses);
}
