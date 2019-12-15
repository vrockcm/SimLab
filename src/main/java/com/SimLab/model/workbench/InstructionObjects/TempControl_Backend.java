package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;

import java.util.List;

public class TempControl_Backend implements InstructionBkend {

    public String material;
    public int targetTemp;
    public String vessel;

    @Override
    public void verify(List<Interaction> interactions, int startIndex) {
        return ;
    }

    @Override
    public boolean getVerified() {
        return false;
    }

    @Override
    public String getMessage() {
        return null;
    }
}