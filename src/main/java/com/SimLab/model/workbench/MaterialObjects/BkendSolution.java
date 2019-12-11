package com.SimLab.model.workbench.MaterialObjects;


import com.SimLab.model.dao.Solution;
import lombok.Data;

@Data
public class BkendSolution {


    private Solution solution;

    public BkendSolution(Solution solution){
        this.solution = solution;
    }


}
