package com.SimLab.model.workbench;

public class TempControl_Backend implements Instruction{

    public String material;
    public int targetTemp;
    public String vessel;

    @Override
    public String verify() {
        return null;
    }
}
