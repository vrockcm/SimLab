package com.SimLab.model.workbench;

import com.SimLab.model.dao.Container;

public class Swirl_Backend implements Instruction {

    public Container container;
    public int targetDuration;


    @Override
    public String verify() {
        return null;
    }
}
