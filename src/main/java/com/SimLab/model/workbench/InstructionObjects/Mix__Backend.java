package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendSolvents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Mix__Backend implements InstructionBkend {

    private List<BkendContainer> contlist;
    private int expectedVol;
    private List<String> namesToCheck;
    int stepNo;

    public Mix__Backend(List<BkendContainer> containers, String solution1, String solution2, int expectedVol, int stepNo){
        contlist = containers;
        this.expectedVol = expectedVol;
        this.stepNo = stepNo;

        namesToCheck = new ArrayList<String>();
        if(solution1.length() >=7 && solution1.substring(0,7).equals("Solvent")){
            int step = Character.getNumericValue(solution1.charAt(7));
            List<BkendSolution> sols = BkendSolvents.getSolventSolutions(step-1);
            namesToCheck.addAll(sols.stream().map(BkendSolution::getSolutionName).collect(Collectors.toList()));
        }else{
            namesToCheck.add(solution1);
        }
        if(solution2.length() >=7 && solution2.substring(0,7).equals("Solvent")){
            int step = Character.getNumericValue(solution2.charAt(7));
            List<BkendSolution> sols = BkendSolvents.getSolventSolutions(step-1);
            namesToCheck.addAll(sols.stream().map(BkendSolution::getSolutionName).collect(Collectors.toList()));
        }else{
            namesToCheck.add(solution2);
        }
    }

    @Override
    public boolean verify() {
        boolean verified = true;
        for(BkendContainer cont : contlist){
            List<String> solutionsInCont = cont.getSolutions().stream().map(BkendSolution::getSolutionName).collect(Collectors.toList());
            if(solutionsInCont.size() == namesToCheck.size()) {
                for (String s: solutionsInCont){
                    if(!namesToCheck.contains(s)){
                        verified = false;
                        break;
                    }
                }
                if(verified && cont.getCumulativeVolume() == expectedVol){
                    cont.setAssociatedStep(stepNo);
                    BkendSolvents.addSolvent(cont.getSolutions());
                    return true;
                }
            }
        }
        return false;
    }
}
