package com.SimLab.model.workbench.InstructionObjects;

public class TempControl_Backend implements InstructionBkend {

    public String material;
    public int targetTemp;
    public String vessel;

    @Override
    public boolean verify() {
        return false;
    }
}