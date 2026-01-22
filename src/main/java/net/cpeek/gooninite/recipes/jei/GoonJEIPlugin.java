package net.cpeek.gooninite.recipes.jei;


import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.recipes.GooniniteRecipes;
import net.cpeek.gooninite.recipes.jei.categories.GoonPressingCategory;
import net.cpeek.gooninite.recipes.jei.categories.LatticeRecrystallizerCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@JeiPlugin
public class GoonJEIPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(Gooninite.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        reg.addRecipeCategories(new LatticeRecrystallizerCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new GoonPressingCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        Level level = Minecraft.getInstance().level;
        if(level == null) return;

        var recrystallizingRecipes = level.getRecipeManager().getAllRecipesFor(
                GooniniteRecipes.LATTICE_RECRYSTALLIZING_RECIPE.get());

        reg.addRecipes(LatticeRecrystallizerCategory.RECIPE_TYPE, recrystallizingRecipes);

        var pressingRecipes = level.getRecipeManager().getAllRecipesFor(
                GooniniteRecipes.GOON_PRESSING_RECIPE.get());
        reg.addRecipes(GoonPressingCategory.RECIPE_TYPE, pressingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(
                new ItemStack(GooniniteBlocks.LATTICE_RECRYSTALLIZER.get()),
                LatticeRecrystallizerCategory.RECIPE_TYPE
        );

        reg.addRecipeCatalyst(
                new ItemStack(GooniniteBlocks.MECHANICAL_SINTER_PRESS.get()),
                GoonPressingCategory.RECIPE_TYPE
        );
    }
}
