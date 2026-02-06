package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.blocks.handlers.GoonBaseSlot;
import net.cpeek.gooninite.items.GooniniteItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class GoonLinerSlot extends GoonBaseSlot {
    public GoonLinerSlot(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y, s-> s.is(GooniniteItems.GOONINITE_LINER_ITEM.get()), 1);
    }
}
