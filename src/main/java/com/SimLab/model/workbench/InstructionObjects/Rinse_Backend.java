package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;

import java.util.List;

public class Rinse_Backend implements InstructionBkend {
    public String material;

    @Override
    public int verify(List<Interaction> interactions, int startIndex) {
        return 0;
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