package com.SimLab.model.workbench;

import com.SimLab.model.dao.*;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class WorkbenchBkend {

    private final String DIFF_CONTAINER = "Beaker";
    private final int DIFF_CAPACITY = 50;

    List<BkendContainer> containers;
    private int containerId = 1;

    List<BkendTool> tools;
    private int toolId = 1;

    Lab lab;
    Instruction currentInst;


    public WorkbenchBkend(Lab lab){
        this.lab = lab;
        containers = new ArrayList<BkendContainer>();
        tools = new ArrayList<BkendTool>();
        for(Instruction i: lab.getInstructions()){
            if(i.getStepNumber() == 1){
                currentInst = i;
                break;
            }
        }
    }

    public String addMaterial(String matName){
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

        for (Tool t : lab.getTools()) {
            if(t.getName().equals(matName)){
                return addTool(t);
            }
        }
        return "";
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

    public void nextStep(){
        int stepNo = currentInst.getStepNumber();
        for(Instruction i: lab.getInstructions()){
            if(i.getStepNumber() == stepNo+1){
                currentInst = i;
                break;
            }
        }
    }
    public void mixInteract(String container1, String container2, int pourAmount){
        BkendContainer cont1 = getContainer(container1);
        BkendContainer cont2 = getContainer(container2);

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

    }

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


    private String addContainer(Container c){
        String contName = generateName(c.getName(), true);
        BkendContainer bkendContainer = new BkendContainer(contName, c.getCapacity());
        containers.add(bkendContainer);
        return contName;
    }

    private String addSolution(Solution s){
        BkendSolution bkendSolution = new BkendSolution(s);
        String contName = generateName(DIFF_CONTAINER, true);
        BkendContainer bkendContainer = new BkendContainer(contName, bkendSolution, DIFF_CAPACITY);
        containers.add(bkendContainer);
        return contName;
    }

    private String addTool(Tool t){
        String toolName = generateName(t.getName(), false);
        BkendTool bkendTool = new BkendTool(toolName);
        tools.add(bkendTool);
        return toolName;

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

    private BkendContainer getContainer(String name){
        for(BkendContainer container: containers){
            if(container.getName().equals(name)) return container;
        }
        return null;
    }
    private BkendTool getTool(String name){
        for(BkendTool tool: tools){
            if(tool.getName().equals(name)) return tool;
        }
        return null;
    }

}
