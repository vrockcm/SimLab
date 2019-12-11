package com.SimLab.model.workbench.MaterialObjects;


import com.SimLab.model.dao.Tool;
import lombok.Data;

@Data
public class BkendTool {

//    private String name;
    private Tool tool;

    public BkendTool(Tool tool){
        this.tool = tool;
    }
}
