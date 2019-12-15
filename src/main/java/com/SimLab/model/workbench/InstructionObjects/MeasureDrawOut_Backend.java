package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;

import java.util.List;

public class MeasureDrawOut_Backend implements InstructionBkend {
//    public String material;
    public BkendTool measuringTool;
    public BkendSolution mysol;
    public BkendContainer mycont;
    public List<BkendContainer> contlist;
    public List<BkendSolution> solslist;
    public int targetvol;

    public MeasureDrawOut_Backend(BkendTool tool, BkendContainer container, BkendSolution solution, int targetVolume){
        this.mysol = solution;
        this.measuringTool = tool;
        this.targetvol = targetVolume;
        this.mycont = container;
    }

    @Override
    public void verify(List<Interaction> interactions, int startIndex) {
//        for(BkendContainer c: contlist){
//            if(c.getName().equals(mycont.getName())){
//                for(BkendSolution s: c.getSolutions()){
//                    if((s.getSolution().getName().equals(mysol.getSolution().getName()))
//                    && mysol.getSolution().getVolume() == targetvol){
//                        return true;
//                    }
//                }
//            }
//
//        }

        return ;
    }

    @Override
    public boolean getVerified() {
        return false;
    }

    @Override
    public String getMessage() {
        return null;
    }
}