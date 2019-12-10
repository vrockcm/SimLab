package com.SimLab.model.workbench;

import com.SimLab.model.dao.Tool;

public class MeasureDrawOut_Backend implements Instruction {
    public String material;
    public Tool measuringTool;

    @Override
    public String verify() {
        return null;
    }
}
