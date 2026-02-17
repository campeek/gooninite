package net.cpeek.gooninite.recipes.jei.categories;


import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.cpeek.gooninite.menus.GoonGUIHelpers;
import net.cpeek.gooninite.menus.HoverRegion;
import net.cpeek.gooninite.recipes.BaseGoonRecipe;
import net.cpeek.gooninite.recipes.LatticeRecrystallizingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class LatticeRecrystallizerCategory extends GoonBaseCategory {
    public static final ResourceLocation UID = new ResourceLocation(Gooninite.MODID, "lattice_recrystallizer");

    private static final HoverRegion ENERGY_BAR_REGION = new HoverRegion(144, 7, 150, 51);

    public static final RecipeType<LatticeRecrystallizingRecipe> RECIPE_TYPE =
            new RecipeType<>(UID, LatticeRecrystallizingRecipe.class);


    public LatticeRecrystallizerCategory(IGuiHelper guiHelper){
        super(guiHelper, GooniniteBlocks.LATTICE_RECRYSTALLIZER.get());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BaseGoonRecipe recipe, IFocusGroup focuses) {
        if(recipe instanceof LatticeRecrystallizingRecipe rec) {
            builder.addSlot(RecipeIngredientRole.INPUT,
                    61, 22).addIngredients(recipe.ingredient());

            builder.addSlot(RecipeIngredientRole.INPUT,
                    15,7)
                    .addIngredient(ForgeTypes.FLUID_STACK, rec.ingredientFluid())
                    .setFluidRenderer(rec.ingredientFluid().getAmount(), false, 18,45);


            builder.addSlot(RecipeIngredientRole.OUTPUT,
                            106, 22)
                    .addItemStack(rec.getOutputItem());
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, BaseGoonRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        if(ENERGY_BAR_REGION.isInside((int) mouseX, (int) mouseY)){
            tooltip.add(FormattedText.of("Energy"));
            tooltip.add(Component.literal(recipe.energy + " FE").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected void setBackground(IGuiHelper helper) {
        IDrawableBuilder builder = helper.drawableBuilder(new ResourceLocation(Gooninite.MODID, "textures/gui/jei/lattice_recrystallizer.png"),
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
