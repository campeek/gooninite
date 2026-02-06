package net.cpeek.gooninite.blocks.handlers;


import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Use for machine outputs.
 * mayPlace always returns false.
 *
 */

public class GoonOutputSlot extends SlotItemHandler {
    public GoonOutputSlot(IItemHandler handler, int pSlot, int pX, int pY) {
        super(handler, pSlot, pX, pY);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }
}
