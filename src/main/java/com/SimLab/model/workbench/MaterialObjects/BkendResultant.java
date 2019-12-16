package com.SimLab.model.workbench.MaterialObjects;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BkendResultant {

    public static List<BkendContainer> solvents = new ArrayList<BkendContainer>();

    public static void addSolvent(BkendContainer add){
        solvents.add(add);
    }
    public static BkendContainer getSolventSolutions(int i){ return solvents.get(i);}

}
