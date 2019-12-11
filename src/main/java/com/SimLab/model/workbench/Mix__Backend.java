package com.SimLab.model.workbench;

import com.SimLab.model.dao.Instruction;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;

import java.util.List;

public class Mix__Backend extends Instruction implements InstructionInterf{
    public String material1;
    public String material2;

    Mix__Backend(List<BkendContainer> containers, List<BkendSolution> solutions, String sol1, String sol2){

    }




    @Override
    public boolean verify() {
//        if(currentInstruc.g)


        return false;
    }
}
