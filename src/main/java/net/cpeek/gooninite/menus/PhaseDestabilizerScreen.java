package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PhaseDestabilizerScreen extends AbstractContainerScreen<PhaseDestabilizerMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Gooninite.MODID, "textures/gui/gui_base.png");
    private static final ResourceLocation BARS_OVERLAY = ResourceLocation.fromNamespaceAndPath(Gooninite.MODID, "textures/gui/hyperbolic_goon_chamber_bars_overlay.png");
    private static final ResourceLocation SLOTS_OVERLAY = ResourceLocation.fromNamespaceAndPath(Gooninite.MODID, "textures/gui/slots_overlay.png");

    private static final int FLUID_BAR_WIDTH = 18;
    private static final int ENERGY_BAR_WIDTH = 7;
    private static final int BAR_HEIGHT = 45;

    public PhaseDestabilizerScreen(PhaseDestabilizerMenu menu, Inventory inv, Component title){
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
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

        gg.blit(BARS_OVERLAY, leftPos, topPos, 1, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        gg.blit(SLOTS_OVERLAY, leftPos, topPos, 1, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);


        int progress = menu.getMaxProgress() == 0 ? 0 : (menu.getProgress()*24/ menu.getMaxProgress());
        /*gg.blit(
                GUI_TEXTURE,
                leftPos + 79,   // x offset for bar position
                topPos + 34,             // y offest for bar position
                176,                     // bar texture is located off the side of the screen
                0,
                progress, 16);  // bar texture gets wider with progress*/

        int energy = menu.getMaxEnergy() == 0 ? 0 : (menu.getEnergy() * 60/ menu.getMaxEnergy());
        /*gg.blit(GUI_TEXTURE,
                leftPos + 10, topPos + 70 - energy,
                176, 16,
                8, energy);

         */
    }


}
