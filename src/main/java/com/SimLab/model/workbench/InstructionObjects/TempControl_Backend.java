package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.dao.Container;
import com.SimLab.model.workbench.InstructionTemplates;
import com.SimLab.model.workbench.Interaction;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendResultant;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class TempControl_Backend implements InstructionBkend {

    private final String MSG1 = "You never had the right solution to heat or cool";
    private final String MSG2 = "You had the right solution but did not heat or cool it to the right temperature";

    private double targetTemp;
    private double targetVolume;
    private boolean verified;
    private List<String> namesToCheck;
    boolean msg2;
    private String message;
    private int stepNo;
    private String name;

    public TempControl_Backend(String solution, List<String> contNames, int stepNom, double targetTemp, String name){
        verified = false;
        msg2 = false;
        this.stepNo = stepNo;
        this.targetTemp = targetTemp;
        this.name = name;

        namesToCheck = new ArrayList<String>();
        if(!contNames.contains(solution)) namesToCheck.add(solution);
    }


    @Override
    public void verify(List<Interaction> interactions, int startIndex) {
        handleResultants();
        for(int i=startIndex; i<interactions.size(); i++){
            Interaction interaction = interactions.get(i);
            if(interaction.getStepNo() != 0 ) continue;
            if(!interaction.getName().equals(InstructionTemplates.HEAT) &&
                !interaction.getName().equals(InstructionTemplates.COOL)) continue;
            verifyResultant(interaction, interaction.getResultant1());
            if(verified) break;
        }
        if(!verified && !msg2){
            message = MSG1;
        }
    }

    private void handleResultants() {
        Set<String> newNames = new HashSet<String>();
        for(String s: namesToCheck){
            if(s.length() >=9 && s.substring(0,9).equals("Resultant")){
                int step = Integer.parseInt(s.substring(9));
                if(BkendResultant.solvents.size() >= step) {
                    BkendContainer sols = BkendResultant.getSolventSolutions(step - 1);
                    newNames.addAll(sols.getSolutions().stream().map(BkendSolution::getSolutionName).collect(Collectors.toList()));
                    targetVolume = sols.getCumulativeVolume();
                }
            }else{
                newNames.add(s);
                targetVolume = -1;
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

            if(verified && (targetVolume == resultant.getCumulativeVolume() || targetVolume<0)){
                if(name.equals(InstructionTemplates.HEAT) && resultant.getCumulativeTemperature() >= targetTemp){
                    verified = true;
                    BkendResultant.addSolvent(resultant);
                    interaction.setStepNo(stepNo);
                }else if(name.equals(InstructionTemplates.COOL) && resultant.getCumulativeTemperature() <= targetTemp){
                    verified = true;
                    BkendResultant.addSolvent(resultant);
                    interaction.setStepNo(stepNo);
                }
                else{
                    verified = false;
                    msg2 = true;
                    message = MSG2;
                }
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