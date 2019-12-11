package com.SimLab.model.workbench;

import com.SimLab.model.dao.*;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendSolution;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorkbenchBkend {

    private final String DIFF_CONTAINER = "Beaker";
    private final int DIFF_CAPACITY = 50;

    List<BkendContainer> containers;
    private int containerId = 1;

    List<BkendTool> tools;
    private int toolId = 1;

    Lab lab;



    public WorkbenchBkend(Lab lab){
        this.lab = lab;
        containers = new ArrayList<BkendContainer>();
        tools = new ArrayList<BkendTool>();
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

        for (Container c : lab.getContainers()) {
            if(c.getName().equals(matName)){
                removeContainer(c);
            }
        }

        for (Tool t : lab.getTools()) {
            if(t.getName().equals(matName)){
                removeTool(t);
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

    private void removeContainer(Container c){
        containers.remove(c);

    }


    private void removeTool(Tool t){

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
