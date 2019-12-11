package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendSolvents;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Mix__Backend implements InstructionBkend {

    private boolean verified;
    private int expectedVol;
    private List<String> namesToCheck;
    int stepNo;

    public Mix__Backend(String solution1, String solution2, int expectedVol, int stepNo){
        this.expectedVol = expectedVol;
        this.stepNo = stepNo;
        verified = false;

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
    public int verify(List<Interaction> interactions, int startIndex) {
        int returnIndex = 0;
        for(int i=startIndex;i<interactions.size();i++){
            Interaction interaction = interactions.get(i);
            List<BkendSolution> resultSolutions = interaction.getResultant().getSolutions();
            boolean verify = true;
            if(resultSolutions.size() == namesToCheck.size()){
                for(String name: resultSolutions.stream().map(BkendSolution::getSolutionName).collect(Collectors.toList())) {
                    if (!namesToCheck.contains(name)){
                        verify = false;
                    }
                }
            }else{
                verify = false;
            }
            if(interaction.getResultant().getCumulativeVolume() == expectedVol && verify){
                verified = true;
                returnIndex = i;
            }
        }
        return returnIndex;
    }

    @Override
    public boolean getVerified() {
        return verified;
    }
}
