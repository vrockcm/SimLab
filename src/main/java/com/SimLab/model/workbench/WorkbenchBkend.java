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

}
