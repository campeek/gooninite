package net.cpeek.gooninite.menus;


import net.cpeek.gooninite.blocks.handlers.GoonOutputSlot;
import net.cpeek.gooninite.blocks.machines.hgc.GoonChamberPhase;
import net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberControllerBE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class HyperbolicGoonChamberMenu extends AbstractContainerMenu {

    private final HyperbolicGoonChamberControllerBE entity;
    private final ContainerData data;

    private final int NUGGET_TL = 0;
    private final int NUGGET_TR = 1;
    private final int NUGGET_BR = 2;
    private final int NUGGET_BL = 3;
    private final int LINER_SLOT = 4;
    private final int OUTPUT_SLOT = 5;

    public HyperbolicGoonChamberMenu(int id, Inventory inv, FriendlyByteBuf buf){
        this(id, inv, (HyperbolicGoonChamberControllerBE)inv.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(9));
    }

    public HyperbolicGoonChamberMenu(int id, Inventory inv, HyperbolicGoonChamberControllerBE entity, ContainerData data){
        super(GooniniteMenus.HYPERBOLIC_GOON_CHAMBER_MENU.get(), id);
        this.entity = entity;
        this.data = data;


        this.addSlot(new GoonPelletSlot(entity.getItemHandler(), NUGGET_TL, 11, 21));
        this.addSlot(new GoonPelletSlot(entity.getItemHandler(), NUGGET_TR, 31, 21));
        this.addSlot(new GoonPelletSlot(entity.getItemHandler(), NUGGET_BR, 31, 41));
        this.addSlot(new GoonPelletSlot(entity.getItemHandler(), NUGGET_BL, 11, 41));
        this.addSlot(new GoonLinerSlot(entity.getItemHandler(), LINER_SLOT, 96, 46));
        this.addSlot(new GoonOutputSlot(entity.getItemHandler(), OUTPUT_SLOT, 96, 21));

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);


        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            System.out.println("Shift-click slot " + index + " : " + stack);

            final int PLAYER_INV_START = 6;
            final int PLAYER_INV_END = PLAYER_INV_START + 27; // player inv is 27 slots big

            final int HOTBAR_START = PLAYER_INV_END;
            final int HOTBAR_END = HOTBAR_START + 9;

            if (index < PLAYER_INV_START) { // machine -> player
                if (!this.moveItemStackTo(stackInSlot, PLAYER_INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else { // player -> machine
                if(!this.moveItemStackTo(stackInSlot, 0, PLAYER_INV_START, false)){
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos()),
                player, entity.getBlockState().getBlock());
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
    private void addPlayerHotbar(Inventory inv){
        for(int col=0; col<9; col++){
            this.addSlot(new Slot(inv, col, 8+col*18, 142));
        }
    }


    public int getCharge(){return data.get(0);}
    public int getMaxCharge(){return data.get(1);}
    public GoonChamberPhase getPhase(){
        return switch(data.get(2)){
            case 0 -> GoonChamberPhase.IDLE;
            case 1 -> GoonChamberPhase.CHARGING;
            case 2 -> GoonChamberPhase.DISCHARGING;
            case 3 -> GoonChamberPhase.COOLDOWN;
            default -> null;
        };
    }
    public int getEnergy(){
        return (data.get(3) << 16 | (data.get(4)&0xffff));
    }
    public int getMaxEnergy(){
        return (data.get(5) << 16 | (data.get(6)&0xffff));
    }
    public int getFluid(){
        return data.get(7);}
    public int getMaxFluid(){return data.get(8);}
}
