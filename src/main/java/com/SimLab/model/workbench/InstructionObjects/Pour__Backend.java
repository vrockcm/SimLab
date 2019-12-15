package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.dao.Solution;
import com.SimLab.model.workbench.Interaction;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendResultant;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Pour__Backend implements InstructionBkend {

    private final String MSG1 = "You did not pour the correct solutions";
    private final String MSG2 = "You poured the correct solutions but your measurement was off by ";

    private boolean verified;
    boolean msg2;
    private int expectedVol;
    private List<String> namesToCheck;
    int stepNo;
    String message;

    public Pour__Backend(String solution1, String solution2, int expectedVol, int stepNo, List<String> contNames){
        this.expectedVol = expectedVol;
        this.stepNo = stepNo;
        verified = false;
        msg2 = false;

        namesToCheck = new ArrayList<String>();
        if(!contNames.contains(solution1)) namesToCheck.add(solution1);
        if(!contNames.contains(solution2)) namesToCheck.add(solution2);

    }

    @Override
    public int verify(List<Interaction> interactions, int startIndex) {
        handleResultants();;
        int returnIndex = 0;

        for(int i=startIndex;i<interactions.size();i++){
            Interaction interaction = interactions.get(i);
            verifyResultant(interaction, interaction.getResultant1());
            if(!verified) verifyResultant(interaction, interaction.getResultant2());
            if(verified){
                returnIndex = i+1;
                break;
            }
        }
        if(!verified && !msg2){
            message = MSG1;
        }

        return returnIndex;
    }

    private void handleResultants(){
        Set<String> newNames = new HashSet<String>();
        for(String s: namesToCheck){
            if(s.length() >=9 && s.substring(0,9).equals("Resultant")){
                int step = Integer.parseInt(s.substring(9));
                if(BkendResultant.solvents.size() >= step) {
                    List<BkendSolution> sols = BkendResultant.getSolventSolutions(step - 1);
                    newNames.addAll(sols.stream().map(BkendSolution::getSolutionName).collect(Collectors.toList()));
                }
            }else{
                newNames.add(s);
            }
        }

        namesToCheck = new ArrayList<String>(newNames);
    }

    private void verifyResultant(Interaction interaction, BkendContainer resultant){
        List<BkendSolution> resultSolutions = resultant.getSolutions();
        verified = false;
        if(resultSolutions.size() == namesToCheck.size()){
            verified = true;
            for(String name: resultSolutions.stream().map(BkendSolution::getSolutionName).collect(Collectors.toList())) {
                if (!namesToCheck.contains(name)){
                    verified = false;
                }
            }
        }
        double expectedVolToCheck = expectedVol + interaction.getContainer2().getCumulativeVolume();
        if(verified) {
            if (resultant.getCumulativeVolume() == expectedVolToCheck) {
                verified = true;
                BkendResultant.addSolvent(resultant.getSolutions());
            } else {
                verified = false;
                msg2 = true;
                double diff = resultant.getCumulativeVolume() - expectedVolToCheck;
                message = MSG2 + Math.abs(diff) + "mL";
            }
        }
    }

    @Override
    public boolean getVerified() {
        return verified;
    }

    public String getMessage(){
        return message;
    }
}
