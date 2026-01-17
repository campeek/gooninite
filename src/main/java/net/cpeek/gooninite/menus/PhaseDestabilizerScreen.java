package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.cpeek.gooninite.menus.GoonGUIHelpers.GUI_TEXTURE;
import static net.cpeek.gooninite.menus.GoonGUIHelpers.SLOTS_OVERLAY;

@SuppressWarnings("removal")
public class PhaseDestabilizerScreen extends AbstractContainerScreen<PhaseDestabilizerMenu> {

    private static final ResourceLocation DESTABILIZER_OVERLAY = new ResourceLocation(Gooninite.MODID, "textures/gui/phase_destabilizer_overlay.png");
    private static final ResourceLocation FLUID_TEXTURE = new ResourceLocation("minecraft", "textures/block/water_still.png");

    private static final int FLUID_BAR_WIDTH = 31;
    private static final int ENERGY_BAR_WIDTH = 7;
    private static final int BAR_HEIGHT = 45;
    private static final int PROGRESS_ARROW_WIDTH = 22;

    private static final HoverRegion FLUID_BAR_REGION = new HoverRegion(103, 20, 133, 64);
    private static final HoverRegion ENERGY_BAR_REGION = new HoverRegion(159, 20, 165, 64);

    private int animTick;

    public PhaseDestabilizerScreen(PhaseDestabilizerMenu menu, Inventory inv, Component title){
        super(menu, inv, title);
        this.imageWidth = 205;
        this.imageHeight = 166;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        animTick++;
    }

    @Override
    public void render(GuiGraphics gg, int mX, int mY, float pPartialTick) {
        renderBackground(gg);
        super.render(gg, mX, mY, pPartialTick);
        renderTooltip(gg, mX, mY);

        int guiMX = mX - leftPos;
        int guiMY = mY - topPos;
        if(FLUID_BAR_REGION.isInside(guiMX, guiMY)){
            gg.renderTooltip(font,
                    Component.literal("Liquid Gooninite: " + menu.getFluid() + "/"+menu.getMaxFluid() + " mB"),
                    mX,
                    mY);
        } else if(ENERGY_BAR_REGION.isInside(guiMX, guiMY)){
            gg.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergy() + "/" + menu.getMaxEnergy() + " FE"),
                    mX,
                    mY);
        }
    }

    protected void renderBg(GuiGraphics gg, float partialTick, int mouseX, int mouseY){
        int x = leftPos;
        int y = topPos;

        GoonGUIHelpers.blit9Slice(gg,
                GUI_TEXTURE,    // texture to draw
                leftPos, topPos,
                176, 166,           // GUI window size (vanilla is 176x166)
                0 ,0,                   // UV in texture
                64, 64,             // how big 9-slice panels are
                8,                          // corner size
                256, 256);              // how big the texture is

        gg.blit(DESTABILIZER_OVERLAY, leftPos, topPos, 1, 0, 0, 176, imageHeight, imageWidth, imageHeight);
        gg.blit(SLOTS_OVERLAY, leftPos, topPos, 1, 0, 0, 176, imageHeight, 176, imageHeight);

        float progressRatio = (float)menu.getProgress()/ menu.getMaxProgress();
        int progressPixels = (int)(PROGRESS_ARROW_WIDTH*progressRatio);

        gg.blit(DESTABILIZER_OVERLAY,
                leftPos+77, topPos+40,
                2,
                183, 159,
                progressPixels, 7,
                imageWidth, imageHeight);

        float energyRatio = (float)menu.getEnergy()/menu.getMaxEnergy();
        int energyPixels = (int)(BAR_HEIGHT*energyRatio);

        int energyBarRootY = topPos+65-energyPixels;
        gg.blit(DESTABILIZER_OVERLAY,
                leftPos+159, energyBarRootY,
                2,
                176, 165-energyPixels,
                ENERGY_BAR_WIDTH, energyPixels,
                imageWidth, imageHeight);

        float fluidRatio = (float)menu.getFluid()/ menu.getMaxFluid();
        int fluidBarPixels = (int)(BAR_HEIGHT*fluidRatio);


        int frame = (animTick/2)%32;
        int fluidBarRootY = topPos+65-fluidBarPixels;
        GoonGUIHelpers.blitTiledTextureAnimated(gg, FLUID_TEXTURE,
                leftPos+103, fluidBarRootY,
                FLUID_BAR_WIDTH, fluidBarPixels,
                16, 512, frame, 16);



    }


}
