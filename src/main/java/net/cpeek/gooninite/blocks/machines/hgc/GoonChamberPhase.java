package net.cpeek.gooninite.blocks.machines.hgc;

public enum GoonChamberPhase{
    IDLE(0),
    CHARGING(1),
    DISCHARGING(2),
    COOLDOWN(3);

    private final int index;
    GoonChamberPhase(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
