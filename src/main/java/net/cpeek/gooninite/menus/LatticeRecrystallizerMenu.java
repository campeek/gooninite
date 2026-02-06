package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.blocks.machines.lattice_recrystallizer.LatticeRecrystallizerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class LatticeRecrystallizerMenu extends AbstractContainerMenu {

    private final LatticeRecrystallizerBlockEntity entity;
    private final ContainerData data;

    public LatticeRecrystallizerMenu(int id, Inventory inv, FriendlyByteBuf buf){
        this(id, inv,
                (LatticeRecrystallizerBlockEntity)inv.player.level().getBlockEntity(buf.readBlockPos()),
        new SimpleContainerData(6));
    }

    public LatticeRecrystallizerMenu(int id, Inventory inv, LatticeRecrystallizerBlockEntity entity, ContainerData data){
        super(GooniniteMenus.LATTICE_RECRYSTALLIZER_MENU.get(), id);
        this.entity = entity;
        this.data = data;

        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 0, 58, 35));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 1, 103, 35));

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

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
                if(isValidItem()){
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

    private boolean isValidItem() {
        return true;
    }

    @Override
    public boolean stillValid(Player player){
        return stillValid(ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos()),
                player, entity.getBlockState().getBlock());
    }

    public int getProgress(){ return data.get(0);}
    public int getMaxProgress(){ return data.get(1); }
    public int getEnergy() { return data.get(2); }
    public int getMaxEnergy(){ return data.get(3); }
    public int getFluid(){ return data.get(4); }
    public int getMaxFluid(){ return data.get(5); }

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
