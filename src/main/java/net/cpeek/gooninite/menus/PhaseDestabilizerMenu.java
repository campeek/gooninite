package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.blocks.machines.phase_destabilizer.PhaseDestabilizerBlockEntity;
import net.cpeek.gooninite.items.GooniniteItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class PhaseDestabilizerMenu extends AbstractContainerMenu {

    private final PhaseDestabilizerBlockEntity entity;
    private final ContainerData data;

    public PhaseDestabilizerMenu(int id, Inventory playerInv, FriendlyByteBuf buf){
        this(id, playerInv,
                (PhaseDestabilizerBlockEntity) playerInv.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(8));
    }

    public PhaseDestabilizerMenu(int id, Inventory playerInv, PhaseDestabilizerBlockEntity entity, ContainerData data){
        super(GooniniteMenus.PHASE_DESTABILIZER_MENU.get(), id);
        this.entity = entity;
        this.data = data;

        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 0, 58, 35));

        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot != null && slot.hasItem()){
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            final int MACHINE_SLOTS = 1;
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
                    if(!this.moveItemStackTo(stackInSlot, 0, MACHINE_SLOTS, false)){
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

    private boolean isGoonNugget(ItemStack stack){
        return stack.is(GooniniteItems.GOONINITE_NUGGET_ITEM.get());
    }

    @Override
    public boolean stillValid(Player player){
        return stillValid(ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos()),
                player, entity.getBlockState().getBlock());
    }

    public int getProgress(){
        return data.get(0);
    }
    public int getMaxProgress(){
        return data.get(1);
    }
    public int getEnergy(){
        return (data.get(2) << 16 | (data.get(3)&0xffff));
    }
    public int getMaxEnergy(){
        return (data.get(4) << 16 | (data.get(5)&0xffff));
    }
    public int getFluid(){ return data.get(6); }
    public int getMaxFluid(){ return data.get(7); }


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

    private void addPlayerHotbar(Inventory inv){
        for(int col=0; col<9; col++){
            this.addSlot(new Slot(inv, col, 8+col*18, 142));
        }
    }
}
