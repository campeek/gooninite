package net.cpeek.gooninite.blocks.handlers;


import net.minecraftforge.energy.EnergyStorage;

public class GoonEnergyStorage extends EnergyStorage {

    public GoonEnergyStorage(int cap, int maxReceive, int maxExtract){
        super(cap, maxReceive, maxExtract);
    }

    public void setEnergy(int energy){
        this.energy = Math.min(energy, getMaxEnergyStored());
    }
}
