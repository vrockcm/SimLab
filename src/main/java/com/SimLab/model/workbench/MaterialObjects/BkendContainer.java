package com.SimLab.model.workbench.MaterialObjects;

import lombok.Data;

@Data
public class BkendContainer {

    private String name;

    private BkendSolution solution;

    private int capacity;

    public BkendContainer(String name, BkendSolution solution, int capacity){
        this.name = name;
        this.solution = solution;
        this.capacity = capacity;
    }
    public BkendContainer(String name, int capacity){
        this.name = name;
        solution = null;
        this.capacity = capacity;
    }



}
