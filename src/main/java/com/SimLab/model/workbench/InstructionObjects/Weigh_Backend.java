package com.SimLab.model.workbench.InstructionObjects;

public class Weigh_Backend implements Instruction{
    public String material;


    @Override
    public boolean verify() {
        return false;
    }
}