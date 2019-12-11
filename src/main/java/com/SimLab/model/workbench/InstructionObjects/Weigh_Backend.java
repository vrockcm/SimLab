package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;

import java.util.List;

public class Weigh_Backend implements InstructionBkend {
    public String material;
    public BkendContainer mycont;
    public BkendSolution mysol;
    public BkendTool mytool;
    public int mytargetweight;

    public Weigh_Backend(BkendContainer container, BkendSolution solution, BkendTool tool, int targetWeight){
        this.mycont = container;
        this.mysol = solution;
        this.mytool = tool;
        this.mytargetweight = targetWeight;

    }


    @Override
    public int verify(List<Interaction> interactions, int startIndex) {
        if (mytool.getTool().getName().equals("Scale")){
            if(Integer.parseInt(mytool.getTool().getAttribute1()) == mytargetweight){
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean getVerified() {
        return false;
    }
}