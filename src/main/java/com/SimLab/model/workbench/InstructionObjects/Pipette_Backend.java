package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.dao.Solution;
import com.SimLab.model.workbench.InstructionTemplates;
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
public class Pipette_Backend implements InstructionBkend {

    DrawUp_Backend drawUp;
    Release_Backend release;

    public Pipette_Backend(String solution1, String solution2, int expectedVol, int stepNo, List<String> contNames){
        drawUp = new DrawUp_Backend(solution1, null, expectedVol, stepNo, contNames);
        release = new Release_Backend(solution2, null, expectedVol, stepNo, contNames);

    }

    @Override
    public void verify(List<Interaction> interactions, int startIndex) {
        drawUp.verify(interactions, startIndex);
        release.verify(interactions, startIndex);
    }



    @Override
    public boolean getVerified() {
        return (drawUp.getVerified() && release.getVerified());
    }

    public String getMessage(){
        String message1 = drawUp.getMessage();
        String message2 = release.getMessage();
        String printMsg = "";
        if(!drawUp.getVerified() && !release.getVerified()){
            printMsg = message1 +" and "+ message2;
        }else if(drawUp.getVerified()){
            printMsg = "You drew the right about but " + message2;
        }else{
            printMsg = message1 + "but somehow released the right amount";
        }
        return printMsg;
    }
}
