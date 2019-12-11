package com.SimLab.model.workbench.MaterialObjects;

import com.SimLab.model.dao.Solution;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BkendContainer {

    private String name;

    private List<BkendSolution> solutions;

    private int capacity;

    public BkendContainer(String name, BkendSolution solution, int capacity){
        solutions = new ArrayList<BkendSolution>();
        this.name = name;
        solutions.add(solution);
        this.capacity = capacity;
    }
    public BkendContainer(String name, int capacity){
        solutions = new ArrayList<BkendSolution>();
        this.name = name;
        this.capacity = capacity;
    }



}
