package com.SimLab.model.workbench.InstructionObjects;

public class Rinse_Backend implements InstructionBkend {
    public String material;

    @Override
    public boolean verify() {
        return false;
    }
}