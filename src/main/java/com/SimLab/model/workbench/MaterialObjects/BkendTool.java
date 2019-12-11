package com.SimLab.model.workbench.MaterialObjects;


import lombok.Data;

@Data
public class BkendTool {

    private String name;

    public BkendTool(String name){
        this.name = name;
    }
}
