package net.cpeek.gooninite.recipes.jei.categories;


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
import net.cpeek.gooninite.menus.HoverRegion;
import net.cpeek.gooninite.recipes.BaseGoonRecipe;
import net.cpeek.gooninite.recipes.PhaseDestabilizingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public class PhaseDestabilizationCategory extends GoonBaseCategory{

    public static final ResourceLocation UID = new ResourceLocation(Gooninite.MODID, "phase_destabilizer");

    public static final RecipeType<PhaseDestabilizingRecipe> RECIPE_TYPE =
            new RecipeType<>(UID, PhaseDestabilizingRecipe.class);
    
    private static final HoverRegion ENERGY_BAR_REGION = new HoverRegion(144, 7, 150, 51);
    
    public PhaseDestabilizationCategory(IGuiHelper guiHelper) {
        super(guiHelper, GooniniteBlocks.PHASE_DESTABILIZER.get());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BaseGoonRecipe recipe, IFocusGroup focuses) {
        if(recipe instanceof PhaseDestabilizingRecipe rec){
            builder.addSlot(RecipeIngredientRole.INPUT,
                            37, 22)
                    .addIngredients(rec.ingredient);

            builder.addSlot(RecipeIngredientRole.OUTPUT,
                    82, 7)
                    .addIngredient(ForgeTypes.FLUID_STACK, rec.resultFluid())
                    .setFluidRenderer(rec.resultFluid().getAmount(), false, 31, 45);
        }
    }

    @Override
    protected void setBackground(IGuiHelper helper) {
        IDrawableBuilder builder = helper.drawableBuilder(new ResourceLocation(Gooninite.MODID, "textures/gui/jei/phase_destabilizer_jei.png"),
                0,0,
                164, 60);
        builder.setTextureSize(164, 60);

        this.background = builder.build();
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
    public RecipeType getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.gooninite.phase_destabilizer");
    }

}
