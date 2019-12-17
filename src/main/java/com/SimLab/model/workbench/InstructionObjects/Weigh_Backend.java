package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.InstructionTemplates;
import com.SimLab.model.workbench.Interaction;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendResultant;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Weigh_Backend implements InstructionBkend {

    private List<String> namesToCheck;
    private boolean verified;
    private double targetVolume;
    private int stepNo;
    private String message;

    public Weigh_Backend(String solution1, int stepNo){
        namesToCheck = new ArrayList<String>();
        namesToCheck.add(solution1);
        this.stepNo = stepNo;
    }


    @Override
    public void verify(List<Interaction> interactions, int startIndex) {
        handleResultants();
        for(int i=startIndex;i<interactions.size();i++){
            Interaction interaction = interactions.get(i);
            if(interaction.getStepNo() != 0) continue;
            if(!interaction.getName().equals(InstructionTemplates.WEIGH)) continue;
            List<BkendSolution> resultSolutions = interaction.getContainer1().getSolutions();
            verified = false;
            if(resultSolutions.size() == namesToCheck.size()){
                verified = true;
                for(String name: resultSolutions.stream().map(BkendSolution::getSolutionName).collect(Collectors.toList())) {
                    if (!namesToCheck.contains(name)){
                        verified = false;
                    }
                }
            }
            if(verified && (targetVolume == interaction.getContainer1().getCumulativeVolume() || targetVolume<0)){
                verified = true;
                BkendResultant.addSolvent(interaction.getContainer1());
                interaction.setStepNo(stepNo);
            }else{
                message = "You did not weigh the correct solution";
            }
        }
    }

    private void handleResultants() {
        Set<String> newNames = new HashSet<String>();
        for (String s : namesToCheck) {
            if (s.length() >= 9 && s.substring(0, 9).equals("Resultant")) {
                int step = Integer.parseInt(s.substring(9));
                if (BkendResultant.solvents.size() >= step) {
                    BkendContainer sols = BkendResultant.getSolventSolutions(step - 1);
                    newNames.addAll(sols.getSolutions().stream().map(BkendSolution::getSolutionName).collect(Collectors.toList()));
                    targetVolume = sols.getCumulativeVolume();
                }
            } else {
                newNames.add(s);
                targetVolume = -1;
            }
        }
    }


    @Override
    public boolean getVerified() {
        return verified;
    }

    @Override
    public String getMessage() {
        return message;
    }
}