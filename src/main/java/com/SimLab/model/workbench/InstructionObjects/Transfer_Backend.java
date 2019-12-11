package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.dao.Container;
import com.SimLab.model.dao.Solution;

public class Transfer_Backend implements Instruction {

    public String material;
    public Solution solution;
    public Container container;

    @Override
    public String verify() {
        return null;
    }
}
