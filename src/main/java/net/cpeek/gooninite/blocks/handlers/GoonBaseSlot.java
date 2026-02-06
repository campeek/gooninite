package net.cpeek.gooninite.blocks.handlers;


import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Predicate;

public class GoonBaseSlot extends SlotItemHandler {
    private final Predicate<ItemStack> stackPredicate;
    private final int maxStack;

    public GoonBaseSlot(IItemHandler handler, int index, int x, int y, Predicate<ItemStack> predicate, int maxStack){
        super(handler, index, x, y);
        this.stackPredicate = predicate;
        this.maxStack = maxStack;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return stackPredicate.test(pStack);
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }
}
