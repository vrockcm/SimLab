package com.SimLab.model.workbench.MaterialObjects;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BkendResultant {

    public static List<List<BkendSolution>> solvents = new ArrayList<List<BkendSolution>>();

    public static void addSolvent(List<BkendSolution> add){
        solvents.add(add);
    }
    public static List<BkendSolution> getSolventSolutions(int i){ return solvents.get(i);}

}
