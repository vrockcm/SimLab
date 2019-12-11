package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.dao.Container;

public class Swirl_Backend implements InstructionBkend {

    public Container container;
    public int targetDuration;


    @Override
    public boolean verify() {
        return false;
    }
}