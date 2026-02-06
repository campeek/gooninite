package net.cpeek.gooninite.blocks.machines.hgc;


import net.minecraft.util.StringRepresentable;

public enum CasingPart implements StringRepresentable {
    SINGLE("single"),
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    private final String name;
    CasingPart(String name){
        this.name = name;
    }


    @Override
    public String getSerializedName() {
        return this.name;
    }
}