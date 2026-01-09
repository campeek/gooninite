package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.blocks.machines.MechanicalSinteringPressBE;
import net.cpeek.gooninite.items.GooniniteItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MechanicalPressMenu extends AbstractContainerMenu {

    private final MechanicalSinteringPressBE entity;
    private final ContainerData data;

    public MechanicalPressMenu(int id, Inventory playerInv, FriendlyByteBuf buf){
        this(id, playerInv,
                (MechanicalSinteringPressBE) playerInv.player.level().getBlockEntity(buf.readBlockPos()));

    }

    public MechanicalPressMenu(int id, Inventory playerInv, MechanicalSinteringPressBE entity){
        super(GooniniteMenus.MECHANICAL_PRESS_MENU.get(), id);
        this.entity = entity;
        this.data = makeData(entity);

        addDataSlots(this.data);
        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);

        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 0, 97, 35));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 1, 142, 35) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {

        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot != null && slot.hasItem()){
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            final int MACHINE_SLOTS = 2;
            final int PLAYER_INV_START = MACHINE_SLOTS;
            final int PLAYER_INV_END = PLAYER_INV_START + 27;

            final int HOTBAR_START = PLAYER_INV_END;
            final int HOTBAR_END = HOTBAR_START + 9;

            if(index < MACHINE_SLOTS){
                // from machine -> player
                if(!this.moveItemStackTo(stackInSlot, PLAYER_INV_START, HOTBAR_END, true)){
                    return ItemStack.EMPTY;
                }
            } else {
                // from player -> machine (if valid item aka GOON NUGGET)
                if(isGoonNugget(stackInSlot)){
                    if(!this.moveItemStackTo(stackInSlot, 0, 1, false)){
                        return ItemStack.EMPTY;
                    }
                } else {
                    // if it's not a goon stack, switch between inventory and hotbar
                    if(index < PLAYER_INV_END){
                        if(!this.moveItemStackTo(stackInSlot, HOTBAR_START, HOTBAR_END, false)){
                            return ItemStack.EMPTY;
                        }
                    } else if(index < HOTBAR_END){
                        if(!this.moveItemStackTo(stackInSlot, PLAYER_INV_START, PLAYER_INV_END, false)){
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if(stackInSlot.isEmpty()){
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if(stackInSlot.getCount() == stack.getCount()){
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos()),
                player, entity.getBlockState().getBlock());
    }


    private boolean isGoonNugget(ItemStack stack){
        return stack.is(GooniniteItems.GOONINITE_NUGGET_ITEM.get());
    }

    private void addPlayerInventory(Inventory inv){
        for(int row=0; row<3; row++){
            for(int col=0; col<9; col++){
                this.addSlot(new Slot(inv,
                        col + row * 9 + 9,
                        8+col*18,
                        84+row*18));
            }
        }
    }

    public int getRPM(){
        return data.get(0);
    }
    public int getProgress(){
        return data.get(1);
    }
    public int getTotalTicks(){
        return data.get(2);
    }

    private void addPlayerHotbar(Inventory inv){
        for(int col=0; col<9; col++){
            this.addSlot(new Slot(inv, col, 8+col*18, 142));
        }
    }

    private static ContainerData makeData(MechanicalSinteringPressBE be){
        return new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch(pIndex){
                    case 0 -> be.getRPM();
                    case 1 -> be.getProgress();
                    case 2 -> be.totalTicks;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0 -> be.setRPM(pValue);
                    case 1 -> be.setProgress(pValue);
                    case 2 -> be.totalTicks = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }
}
