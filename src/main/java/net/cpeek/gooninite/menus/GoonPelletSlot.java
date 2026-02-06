package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.blocks.handlers.GoonBaseSlot;
import net.cpeek.gooninite.items.GooniniteItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class GoonPelletSlot extends GoonBaseSlot {
    public GoonPelletSlot(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y, s -> s.is(GooniniteItems.GOONINITE_PELLET_ITEM.get()), 1);
    }
}
