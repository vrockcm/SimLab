package com.SimLab.model.workbench;

import com.SimLab.model.dao.Container;
import com.SimLab.model.dao.Instruction;
import com.SimLab.model.dao.Solution;

public class Transfer_Backend extends Instruction implements InstructionInterf {

    public String material;
    public Solution solution;
    public Container container;

    @Override
    public boolean verify() {
        return false;
    }
}
