package com.SimLab.model.workbench;

import com.SimLab.model.dao.Instruction;

public class TempControl_Backend implements InstructionInterf{

    public String material;
    public int targetTemp;
    public String vessel;

    @Override
    public boolean verify() {
        return false;
    }
}
