package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.Gooninite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class GoonGUIHelpers {

    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Gooninite.MODID, "textures/gui/gui_base.png");
    public static final ResourceLocation SLOTS_OVERLAY = ResourceLocation.fromNamespaceAndPath(Gooninite.MODID, "textures/gui/slots_overlay.png");

    public static void blit9Slice(GuiGraphics gg, ResourceLocation tex,
                                  int x, int y, int w, int h,               // position + window size
                                  int u, int v, int regionW, int regionH,   // UV position in texture + how big slices are (64x64px)
                                  int c,                                    // corner size
                                  int texW, int texH)                       // how big the 9slice atlas is
    {
        if (w < c*2 || h < c*2) return; // if size is smaller than corners, do nothing

        int edgeHeight = h-(regionH*2);
        int edgeWidth = w-(regionW*2);


        gg.blit(tex, x, y, 0, 0 ,0, regionW, regionH, texW, texH); // top left
        gg.blit(tex, x, y+(h-regionH), 0, 0 ,texH-regionH, regionW, regionH, texW, texH); // bottom left

        gg.blit(tex, x+(regionW+edgeWidth), y, 0,
                (texW-regionW), 0, // texture coordinate offsets
                regionW, regionH,             // width of blitted portion in tex coords
                texW, texH); // top right     // size of texture

        gg.blit(tex, x+(regionW + edgeWidth), y+(h-regionH), 0, (texW-regionW), (texH-regionH), regionW, regionH, texW, texH);


        // edges
        gg.blit(tex, x, y+regionH, 0,
                0, regionH, regionW, edgeHeight, texW, texH); // left

        gg.blit(tex, x+regionW, y,
                regionW, 0, edgeWidth, regionH, texW, texH); // top

        gg.blit(tex, x+(regionW+edgeWidth), y+regionH, 0,
                (texW-regionW), regionH, regionW, edgeHeight, texW, texH); // right

        gg.blit(tex, x+regionW, y+(regionH+edgeHeight), 0,
                regionW, (texH-regionH), edgeWidth, regionH, texW, texH); // bottom

        //center
        gg.blit(tex, x+regionW, y+regionW, 0,
                regionH, regionW, edgeWidth, edgeHeight, texW, texH);

    }
}
