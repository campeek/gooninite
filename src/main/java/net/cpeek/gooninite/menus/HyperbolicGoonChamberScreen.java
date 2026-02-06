package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HyperbolicGoonChamberScreen extends AbstractContainerScreen<HyperbolicGoonChamberMenu> {

    private static final ResourceLocation HGC_OVERLAY = new ResourceLocation(Gooninite.MODID, "textures/gui/hgc_overlay.png");
    private static final ResourceLocation FLUID_TEXTURE = new ResourceLocation("minecraft", "textures/block/water_still.png");

    private static final int FLUID_BAR_WIDTH = 18;
    private static final int CHARGE_BAR_WIDTH = 15;
    private static final int BAR_HEIGHT = 45;

    private static final HoverRegion FLUID_BAR_REGION = new HoverRegion(148,20,165,64);
    private static final HoverRegion CHARGE_BAR_REGION = new HoverRegion(70,20,84,64);
    private static final HoverRegion BUFFER_BAR_REGION = new HoverRegion(58, 20, 64, 64);


    private int animTick;

    public HyperbolicGoonChamberScreen(HyperbolicGoonChamberMenu menu, Inventory inv, Component title){
        super(menu, inv, title);
        this.imageHeight = 166;
        this.imageWidth = 205;
    }

    @Override
    public void render(GuiGraphics gg, int mX, int mY, float partialTick) {
        renderBackground(gg);
        super.render(gg, mX, mY, partialTick);
        renderTooltip(gg, mX, mY);

        int guiMx = mX - leftPos;
        int guiMy = mY - topPos;

        if(FLUID_BAR_REGION.isInside(guiMx, guiMy)){
            gg.renderTooltip(font,
                    Component.literal("Liquid Gooninite: " + menu.getFluid() + "/" +menu.getMaxFluid()+" mB"),
                    mX,
                    mY);
        } else if(CHARGE_BAR_REGION.isInside(guiMx, guiMy)){
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Current Phase: " + menu.getPhase().name()));
            tooltip.add(Component.literal("Current Charge: " + menu.getCharge()+"/"+menu.getMaxCharge() + " FE"));
            gg.renderTooltip(font,
                    tooltip, Optional.empty(),
                    mX,
                    mY);
        } else if(BUFFER_BAR_REGION.isInside(guiMx, guiMy)){
            gg.renderTooltip(font,
                    Component.literal("Energy Buffer: " + menu.getEnergy() + "/" + menu.getMaxEnergy()+ " FE"),
                    mX,
                    mY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics gg, float pPartialTick, int pMouseX, int pMouseY) {
        GoonGUIHelpers.blit9Slice(gg, GoonGUIHelpers.GUI_TEXTURE,
                leftPos, topPos,
                176, 166,
                0, 0,
                64, 64,
                8,
                256, 256);

        gg.blit(HGC_OVERLAY,
                leftPos, topPos,
                1,
                0, 0,
                176, imageHeight,
                imageWidth, imageHeight);

        gg.blit(GoonGUIHelpers.SLOTS_OVERLAY,
                leftPos, topPos,
                1,
                0, 0,
                176, imageHeight, 176, imageHeight);

        float chargeRatio = (float)menu.getCharge()/menu.getMaxCharge();
        int chargeBarPixels = (int)(BAR_HEIGHT*chargeRatio);

        int chargeBarRootY = topPos+65-chargeBarPixels;
        gg.blit(HGC_OVERLAY,
                leftPos+70, chargeBarRootY,
                2,
                176, 165-chargeBarPixels,
                CHARGE_BAR_WIDTH, chargeBarPixels,
                imageWidth, imageHeight);

        float energyRatio = (float)menu.getEnergy()/menu.getMaxEnergy();
        int energyBarPixels = (int)(BAR_HEIGHT*energyRatio);

        int energyBarRootY = topPos+65-energyBarPixels;
        gg.blit(HGC_OVERLAY,
                leftPos+58, energyBarRootY,
                2,
                191, 165-energyBarPixels,
                CHARGE_BAR_WIDTH, energyBarPixels,
                imageWidth, imageHeight);

        float fluidRatio = (float)menu.getFluid()/ menu.getMaxFluid();
        int fluidBarPixels = (int)(BAR_HEIGHT*fluidRatio);


        int frame = (animTick/2)%32;
        int fluidBarRootY = topPos+65-fluidBarPixels;
        GoonGUIHelpers.blitTiledTextureAnimated(gg, FLUID_TEXTURE,
                leftPos+148, fluidBarRootY,
                FLUID_BAR_WIDTH, fluidBarPixels,
                16, 512, frame, 16);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        animTick++;
    }
}
