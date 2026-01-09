package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class MechanicalPressScreen extends AbstractContainerScreen<MechanicalPressMenu> {

    // Resources
    private static final ResourceLocation PRESS_OVERLAY = ResourceLocation.fromNamespaceAndPath(Gooninite.MODID,"textures/gui/mechanical_press_overlay.png");

    /*
    Important numbers
        Bright Bars
            x=176 y=141
            w=43  h=25

        Bright "Pressing" text
            x=176 y=133
            w=45  h=8

        A Singular Ellipse Pixel (lol)
            x=176 y=132

        Bright Flame Icon
            x=212 y=125
            w=9   h=10

        Progress Arrow Fill
            x=177 y=126
            w=22  h=7
     */

    public MechanicalPressScreen(MechanicalPressMenu menu, Inventory inv, Component title){
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private String makeRPMString(int rpm){
        if(rpm == 0) return "00";
        else return Integer.toString(rpm);
    }

    protected void renderBg(GuiGraphics gg, float partialTick, int mouseX, int mouseY){
        int x = leftPos;
        int y = topPos;

        GoonGUIHelpers.blit9Slice(gg,
                GoonGUIHelpers.GUI_TEXTURE,
                leftPos, topPos,
                176, 166,
                0, 0,
                64, 64,
                8,
                256, 256);

        gg.blit(GoonGUIHelpers.SLOTS_OVERLAY,
                leftPos, topPos,
                1,
                0,0,
                imageWidth, imageHeight,
                imageWidth, imageHeight);

        // Blit overlay
        gg.blit(PRESS_OVERLAY,
                leftPos, topPos,
                1,
                0, 0,
                imageWidth, imageHeight,
                221, imageHeight);

        /*gg.drawString(this.font,
                makeRPMString(menu.getRPM()),
                leftPos+65, topPos+33,
                0xffffb84d,
                false);

         */
        gg.drawCenteredString(this.font,
                makeRPMString(menu.getRPM()),
                leftPos+71, topPos+33,
                0xffffb84d);

        // Blit bars for RPM
        int rpm = menu.getRPM();
        int barSegments;
        if(rpm > 128) barSegments = 9;
        else{
            double div = Math.ceil((double)rpm/16);
            barSegments = Mth.clamp((int)div, 0, 8);
        }

        int pixelWidth = 5*barSegments;

        gg.blit(PRESS_OVERLAY,
                leftPos + 17, topPos + 26,
                2,
                176, 141,
                pixelWidth, 25,
                221, imageHeight);

        // Progress arrow
        float progNorm = (float)menu.getProgress()/(float)menu.getTotalTicks();
        int arrowWidth = (int)(22*progNorm);

        //System.out.println(progNorm);
        //System.out.println("progress: " + menu.getProgress());
        //System.out.println("total ticks: " + menu.getTotalTicks());

        gg.blit(PRESS_OVERLAY,
                leftPos + 117, topPos + 39,
                2,
                177, 126,
                arrowWidth, 7,
                221, imageHeight);

    }
}
