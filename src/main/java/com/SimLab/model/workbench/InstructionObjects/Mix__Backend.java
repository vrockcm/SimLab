package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendResultant;
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
        namesToCheck.add(solution1);
        namesToCheck.add(solution2);

    }

    @Override
    public int verify(List<Interaction> interactions, int startIndex) {
        handleResultants();;
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
            double expectedVolToCheck = expectedVol + interaction.getContainer2().getCumulativeVolume();
            if(interaction.getResultant().getCumulativeVolume() == expectedVolToCheck && verify){
                verified = true;
                returnIndex = i+1;
                BkendResultant.addSolvent(interaction.getResultant().getSolutions());
                break;
            }
        }
        return returnIndex;
    }

    private void handleResultants(){
        List<String> newNames = new ArrayList<String>();
        for(String s: namesToCheck){
            if(s.length() >=9 && s.substring(0,9).equals("Resultant")){
                int step = Integer.parseInt(s.substring(9));
                List<BkendSolution> sols = BkendResultant.getSolventSolutions(step-1);
                newNames.addAll(sols.stream().map(BkendSolution::getSolutionName).collect(Collectors.toList()));
            }else{
                newNames.add(s);
            }
        }
        namesToCheck = newNames;
    }

    @Override
    public boolean getVerified() {
        return verified;
    }
}
