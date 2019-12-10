package com.SimLab.model.workbench.MaterialObjects;

import lombok.Data;

@Data
public class BkendContainer {

    private BkendSolution solution;

    private int capacity;

    public BkendContainer(BkendSolution solution, int capacity){
        this.solution = solution;
        this.capacity = capacity;
    }
    public BkendContainer(int capacity){
        solution = null;
        this.capacity = capacity;
    }



}
