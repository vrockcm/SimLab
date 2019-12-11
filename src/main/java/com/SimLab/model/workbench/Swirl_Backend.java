package com.SimLab.model.workbench;

import com.SimLab.model.dao.Container;
import com.SimLab.model.dao.Instruction;

public class Swirl_Backend implements InstructionInterf {

    public Container container;
    public int targetDuration;


    @Override
    public boolean verify() {
        return false;
    }
}
