package com.SimLab.model.workbench;

import com.SimLab.model.dao.*;
import com.SimLab.model.workbench.InstructionObjects.InstructionBkend;
import com.SimLab.model.workbench.InstructionObjects.Pour__Backend;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;
import com.SimLab.service.LabResultService;
import com.SimLab.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class WorkbenchBkend {


    @Autowired
    private LabResultService labResultService;

    @Autowired
    private UserService userService;

    private final String DIFF_CONTAINER = "Beaker";
    private final String POUR = "Pour";
    private final String SWIRL = "Swirl";
    private final String HEAT = "Heat";
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

    public BkendTool addTool(String matName){
        for (Tool t : lab.getTools()) {
            if(t.getName().equals(matName)){
                String toolName = generateName(t.getName(), false);
                BkendTool bkendTool = new BkendTool(t, toolName);
                tools.add(bkendTool);
                return bkendTool;
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

    public List<InstructionBkend> verifyLab(List<String> contNames){
        List<Instruction> instructions = new ArrayList(lab.getInstructions());
        instructions.sort(Comparator.comparing(e -> e.getStepNumber()));
        List<InstructionBkend> instObjs = new ArrayList<InstructionBkend>();
        for(Instruction i: instructions){
            instObjs.add(getInstructionObject(i, contNames));
        }
        int interactIndex = 0;
        for(InstructionBkend iObj: instObjs){
            if(iObj != null) {
                iObj.verify(interactions, interactIndex);
            }
        }

        return instObjs;
    }

    private InstructionBkend getInstructionObject(Instruction currentInst, List<String> contNames){
        InstructionBkend toReturn = null;
        if(currentInst.getName().equals(POUR)){
            Pour__Backend pour = new Pour__Backend(currentInst.getContainer1(), currentInst.getContainer2(),  currentInst.getTargetVolume(), currentInst.getStepNumber(), contNames);
            toReturn = pour;
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

        if(interactName.equals(POUR)){
            List<BkendContainer> resultants = pourInteract(cont1, cont2, pourAmount);
            interaction.addResultant1(resultants.get(0));
            interaction.addResultant2(resultants.get(1));
        }else if(interactName.equals(SWIRL)){
            BkendContainer resultant = swirlInteract(cont1);
            interaction.addResultant1(resultant);
            interaction.addResultant2(null);
        }

        interactions.add(interaction);
    }

    public List<BkendContainer> pourInteract(BkendContainer cont1, BkendContainer cont2, int pourAmount){

        double totalCont1Vol = cont1.getCumulativeVolume();
        for(BkendSolution sol: cont1.getSolutions()){
            double percentage = sol.getVolume()/totalCont1Vol;
            double amtPerSol = pourAmount*percentage;
            sol.setVolume(sol.getVolume()-amtPerSol);
            BkendSolution transfer = new BkendSolution(sol);
            transfer.setVolume(amtPerSol);
            cont2.addSolution(transfer);
        }
        coalesceContainer(cont1);
        coalesceContainer(cont2);
        List<BkendContainer> returnConts = new ArrayList<BkendContainer>();
        returnConts.add(cont1);
        returnConts.add(cont2);
        return returnConts;
    }

    public BkendContainer swirlInteract(BkendContainer container){
        container.setSwirled(true);
        return container;
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
            if(container.getName().equals(name)){
                container.update();
                return container;
            }
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
