package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings("removal")
public class LatticeRecrystallizerScreen extends AbstractContainerScreen<LatticeRecrystallizerMenu> {

    private static final ResourceLocation RECRYSTALLIZER_OVERLAY = new ResourceLocation(Gooninite.MODID, "textures/gui/lattice_recrystallizer_overlay.png");
    private static final ResourceLocation FLUID_TEXTURE = new ResourceLocation("minecraft", "textures/block/water_still.png");

    private static final int FLUID_BAR_WIDTH = 18;
    private static final int ENERGY_BAR_WIDTH = 7;
    private static final int BAR_HEIGHT = 45;
    private static final int PROGRESS_ARROW_WIDTH = 22;


    public LatticeRecrystallizerScreen(LatticeRecrystallizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 166;
        this.imageWidth = 205;
    }

    @Override
    protected void renderBg(GuiGraphics gg, float pPartialTick, int pMouseX, int pMouseY) {

        // draw grey GUI base
        GoonGUIHelpers.blit9Slice(gg, GoonGUIHelpers.GUI_TEXTURE,
                leftPos, topPos,
                176, 166,
                0 ,0,
                64, 64,
                8,
                256, 256);

        gg.blit(GoonGUIHelpers.SLOTS_OVERLAY,
                leftPos, topPos,
                1,
                0, 0,
                176, imageHeight,
                176, imageHeight);

        gg.blit(RECRYSTALLIZER_OVERLAY,
                leftPos, topPos,
                1,
                0,0,
                176, imageHeight,
                imageWidth, imageHeight);

        //gg.

        float progressRatio = (float)menu.getProgress()/menu.getMaxProgress();
        int progressArrowPixels = (int)(PROGRESS_ARROW_WIDTH * progressRatio);

        gg.blit(RECRYSTALLIZER_OVERLAY,
                leftPos+77, topPos+40,
                2,
                183, 159,
                progressArrowPixels, 7,
                imageWidth, imageHeight);

        float energyRatio = (float)menu.getEnergy()/menu.getMaxEnergy();
        int energyBarPixels = (int)(BAR_HEIGHT*energyRatio);

        int energyBarRootY = topPos+65-energyBarPixels;
        gg.blit(RECRYSTALLIZER_OVERLAY,
                leftPos+159, energyBarRootY,
                2,
                176, 165-energyBarPixels,
                ENERGY_BAR_WIDTH, energyBarPixels,
                imageWidth, imageHeight);


        float fluidRatio = (float)menu.getFluid()/ menu.getMaxFluid();
        int fluidBarPixels = (int)(BAR_HEIGHT*fluidRatio);

        int fluidBarRootY = topPos+65-fluidBarPixels;
        GoonGUIHelpers.blitTiledTextureAnimated(gg, FLUID_TEXTURE,
                leftPos+12, fluidBarRootY,
                FLUID_BAR_WIDTH, fluidBarPixels,
                16, 16, 1);

    }
}
