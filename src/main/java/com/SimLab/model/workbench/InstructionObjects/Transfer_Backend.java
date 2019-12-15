package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.dao.Container;
import com.SimLab.model.dao.Solution;
import com.SimLab.model.workbench.Interaction;

import java.util.List;

public class Transfer_Backend implements InstructionBkend {

    public String material;
    public Solution solution;
    public Container container;

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