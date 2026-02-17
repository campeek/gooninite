package net.cpeek.gooninite.recipes.jei.categories;


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
import net.cpeek.gooninite.items.GooniniteItems;
import net.cpeek.gooninite.menus.HoverRegion;
import net.cpeek.gooninite.recipes.BaseGoonRecipe;
import net.cpeek.gooninite.recipes.HyperbolicGoonificationRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class HyperbolicGoonificationCategory extends GoonBaseCategory {

    public static final ResourceLocation UID = new ResourceLocation(Gooninite.MODID, "hyperbolic_goon_chamber");
    public static final RecipeType<HyperbolicGoonificationRecipe> RECIPE_TYPE = new RecipeType<>(UID, HyperbolicGoonificationRecipe.class);

    private static final HoverRegion ENERGY_BAR_REGION = new HoverRegion(144, 7, 150, 51);
    private static final HoverRegion CHARGE_BAR_REGION = new HoverRegion(70, 8, 84, 52);
    private static final ResourceLocation OVERLAY = new ResourceLocation(Gooninite.MODID, "textures/gui/jei/goon_chamber.png");


    public HyperbolicGoonificationCategory(IGuiHelper guiHelper) {
        super(guiHelper, GooniniteBlocks.HYPERBOLIC_GOON_CHAMBER_CONTROLLER.get());
    }

    @Override
    protected void setBackground(IGuiHelper helper) {
        IDrawableBuilder builder = helper.drawableBuilder(OVERLAY,
                0,0,
                164, 60);
        builder.setTextureSize(179, 60);

        this.background = builder.build();
    }

    @Override
    public void draw(BaseGoonRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gg, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, gg, mouseX, mouseY);

        Minecraft mc = Minecraft.getInstance();

        long gameTime = mc.level != null ? mc.level.getGameTime() : 0;

        final int TOTAL_FRAMES = 10;
        final int FRAME_TIME = 10;
        final int FRAME_PIXELS = 45/TOTAL_FRAMES;

        int frame = (int) (gameTime/FRAME_TIME)%TOTAL_FRAMES;

        int energyBarPixels = FRAME_PIXELS*frame+FRAME_PIXELS;
        int energyBarRootY = 52-energyBarPixels;

        System.out.println(energyBarPixels);

        gg.blit(OVERLAY,
                70, energyBarRootY,
                164, 59 - energyBarPixels,
                15, energyBarPixels,
                179, 60);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, BaseGoonRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
        if(ENERGY_BAR_REGION.isInside((int) mouseX, (int) mouseY)){
            tooltip.add(FormattedText.of("Energy"));
            tooltip.add(Component.literal(recipe.energy + " FE").withStyle(ChatFormatting.GRAY));
        } else if(CHARGE_BAR_REGION.isInside((int) mouseX, (int)mouseY)){
            tooltip.add(FormattedText.of("Chamber Charge"));
            tooltip.add(Component.literal("2.4 MFE/tick for 11s").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public RecipeType getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.gooninite.hyperbolic_goon_chamber");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BaseGoonRecipe recipe, IFocusGroup focuses) {
        if(recipe instanceof HyperbolicGoonificationRecipe hg) {
            builder.addSlot(RecipeIngredientRole.INPUT, 19, 13).addIngredients(recipe.ingredient());
            builder.addSlot(RecipeIngredientRole.INPUT, 39, 13).addIngredients(recipe.ingredient());
            builder.addSlot(RecipeIngredientRole.INPUT, 19, 33).addIngredients(recipe.ingredient());
            builder.addSlot(RecipeIngredientRole.INPUT, 39, 33).addIngredients(recipe.ingredient());

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 95, 36).addItemStack(new ItemStack(GooniniteItems.GOONINITE_LINER_ITEM.get()));

            builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 11).addItemStack(hg.resultItem());
        }
    }
}
