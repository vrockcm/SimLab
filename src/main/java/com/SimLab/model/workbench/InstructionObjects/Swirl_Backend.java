package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.dao.Container;
import com.SimLab.model.workbench.Interaction;

import java.util.List;

public class Swirl_Backend implements InstructionBkend {

    public Container container;
    public int targetDuration;


    @Override
    public int verify(List<Interaction> interactions, int startIndex) {
        return 0;
    }

    @Override
    public boolean getVerified() {
        return false;
    }
}