package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;

import java.util.List;

public class Mix__Backend implements Instruction{
    public String solution1;
    public String solution2;
    public BkendContainer con;
    public BkendSolution targetsol;
//    public BkendSolution solution2;
    public List<BkendContainer> contlist;
    public List<BkendSolution> solslist;

    public Mix__Backend(BkendContainer container, BkendSolution targetsolution, String sol1, String sol2){
        this.con = container;
        this.targetsol = targetsolution;
        this.solution1 = sol1;
        this.solution2 = sol2;


    }

    @Override
    public boolean verify() {
        for(BkendContainer c: contlist){
            if(c.getName().equals(con.getName())){
                for(BkendSolution s: c.getSolutions()){
                    if(s.getSolution().getName().equals(targetsol.getSolution().getName())){
                        return true;
                    }
                }
            }

        }

        return false;
    }
}
