package com.SimLab.model.workbench;

import com.SimLab.model.dao.*;
import com.SimLab.model.workbench.InstructionObjects.InstructionBkend;
import com.SimLab.model.workbench.InstructionObjects.Mix__Backend;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class WorkbenchBkend {

    private final String DIFF_CONTAINER = "Beaker";
    private final String MIX = "Mix";
    private final int DIFF_CAPACITY = 50;

    List<BkendContainer> containers;
    private int containerId = 1;

    List<BkendTool> tools;
    private int toolId = 1;

    Lab lab;

    List<Interaction> interactions;

    public WorkbenchBkend(Lab lab){
        this.lab = lab;
        containers = new ArrayList<BkendContainer>();
        tools = new ArrayList<BkendTool>();
        interactions = new ArrayList<Interaction>();

    }

    public BkendContainer addMaterial(String matName){
        for (Solution s : lab.getSolutions()) {
            if(s.getName().equals(matName)){
                return addSolution(s);
            }
        }
        for (Container c : lab.getContainers()) {
            if(c.getName().equals(matName)){
                return addContainer(c);
            }
        }

        return null;
    }

    private String addTool(String matName){
        for (Tool t : lab.getTools()) {
            if(t.getName().equals(matName)){
                String toolName = generateName(t.getName(), false);
                BkendTool bkendTool = new BkendTool(t, toolName);
                tools.add(bkendTool);
                return toolName;
            }
        }
        return null;
    }

    public void removeMaterial(String matName){
        for(BkendContainer container: containers){
            if(container.getName().equals(matName)){
                removeContainer(container);
                break;
            }
        }
        for(BkendTool tool: tools){
            if(tool.getName().equals(matName)){
                removeTool(tool);
                break;
            }
        }
    }

    public List<Boolean> verifyLab(){
        List<Boolean> stepVerified = new ArrayList<Boolean>();
        List<Instruction> instructions = new ArrayList(lab.getInstructions());
        instructions.sort(Comparator.comparing(e -> e.getStepNumber()));
        List<InstructionBkend> instObjs = new ArrayList<InstructionBkend>();
        for(Instruction i: instructions){
            instObjs.add(getInstructionObject(i));
        }
        int interactIndex = 0;
        for(InstructionBkend iObj: instObjs){
            if(iObj != null) {
                int step = iObj.verify(interactions, interactIndex);
                interactIndex = step;
            }
        }
        for(InstructionBkend iObj: instObjs){
            if(iObj != null){
                stepVerified.add(iObj.getVerified());
            }else{
                stepVerified.add(false);
            }
        }
        return stepVerified;
    }

    private InstructionBkend getInstructionObject(Instruction currentInst){
        InstructionBkend toReturn = null;
        if(currentInst.getName().equals(MIX)){
            Mix__Backend mix = new Mix__Backend(currentInst.getContainer1(), currentInst.getContainer2(),  currentInst.getTargetVolume(), currentInst.getStepNumber());
            toReturn = mix;
        }
        return toReturn;
    }

    public List<BkendSolution> getSolutionsInContainer(String name){
        BkendContainer cont = getContainer(name);
        return cont.getSolutions();
    }


    public void interact(String interactName, String container1, String container2, String tool, int pourAmount, int activationDuration){
        BkendContainer cont1 = getContainer(container1);
        BkendContainer cont2 = getContainer(container2);
        BkendTool tool1 = getTool(tool);
        Interaction interaction = new Interaction(interactName, cont1, cont2, tool1);

        if(interactName.equals(MIX)){
            BkendContainer resultant = mixInteract(cont1, cont2, pourAmount);
            interaction.addResultant(resultant);
        }

        interactions.add(interaction);
    }

    public BkendContainer mixInteract(BkendContainer cont1, BkendContainer cont2, int pourAmount){

        double totalCont1Vol = cont1.getCumulativeVolume();
        for(BkendSolution sol: cont1.getSolutions()){
            double percentage = sol.getVolume()/totalCont1Vol;
            double amtPerSol = pourAmount*percentage;
            sol.setVolume(sol.getVolume()-amtPerSol);
            BkendSolution transfer = new BkendSolution(sol);
            transfer.setVolume(amtPerSol);
            cont2.getSolutions().add(transfer);
        }
        coalesceContainer(cont1);
        coalesceContainer(cont2);
        return cont2;
    }

    public void temperatureControlInteract(String container, String tool){

    }



    //############### HELPERS ##############################

    //add duplicate solutions together and remove empty ones
    private void coalesceContainer(BkendContainer container){
        for(int i=0;i<container.getSolutions().size();i++){
            BkendSolution sol = container.getSolutions().get(i);
            for(int j=i+1;j<container.getSolutions().size();j++){
                BkendSolution sol2 = container.getSolutions().get(j);
                if(sol2.getSolutionName().equals(sol.getSolutionName())){
                    sol.setVolume(sol.getVolume()+sol2.getVolume());
                    container.getSolutions().remove(sol2);
                }
            }
            if(sol.getVolume() <= 0){
                container.getSolutions().remove(sol);
            }
        }
    }


    private BkendContainer addContainer(Container c){
        String contName = generateName(c.getName(), true);
        BkendContainer bkendContainer = new BkendContainer(contName, null, c.getCapacity());
        containers.add(bkendContainer);
        bkendContainer.update();
        return bkendContainer;
    }

    private BkendContainer addSolution(Solution s){
        BkendSolution bkendSolution = new BkendSolution(s);
        String contName = generateName(DIFF_CONTAINER, true);
        BkendContainer bkendContainer = new BkendContainer(contName, bkendSolution, DIFF_CAPACITY);
        containers.add(bkendContainer);
        bkendContainer.update();
        return bkendContainer;
    }


    private void removeContainer(BkendContainer c){
        containers.remove(c);
    }


    private void removeTool(BkendTool t){
        tools.remove(t);
    }


    private String generateName(String name, boolean container){
        String nameToReturn = name;
        if(container) {
            nameToReturn += containerId;
            containerId++;
        }else{
            nameToReturn += toolId;
            toolId++;
        }
        return nameToReturn;
    }

    public BkendContainer getContainer(String name){
        for(BkendContainer container: containers){
            if(container.getName().equals(name)) return container;
        }
        return null;
    }
    private BkendTool getTool(String name){
        for(BkendTool tool: tools){
            if(tool.getTool().getName().equals(name)) return tool;
        }
        return null;
    }

}
