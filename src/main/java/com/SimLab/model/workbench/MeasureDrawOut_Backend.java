package com.SimLab.model.workbench;

import com.SimLab.model.dao.Tool;
import com.SimLab.model.dao.Instruction;

public class MeasureDrawOut_Backend implements InstructionInterf {
    public String material;
    public Tool measuringTool;

    @Override
    public boolean verify() {
        return false;
    }
}
