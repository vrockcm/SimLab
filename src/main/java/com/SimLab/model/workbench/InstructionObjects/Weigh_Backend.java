package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;

public class Weigh_Backend implements Instruction{
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
    public boolean verify() {
        if (mytool.getTool().getName().equals("Scale")){
            if(Integer.parseInt(mytool.getTool().getAttribute1()) == mytargetweight){
                return true;
            }
        }
        return false;
    }
}