package com.SimLab.model.workbench.MaterialObjects;


import com.SimLab.model.dao.Tool;
import lombok.Data;

@Data
public class BkendTool implements Cloneable{

    private String name;
    private Tool tool;


    public BkendTool(Tool tool, String name){
        this.tool = tool;
        this.name = name;
    }

    @Override
    public Object clone() {
        BkendTool tool;
        try {
            tool = (BkendTool) super.clone();
        } catch (CloneNotSupportedException e) {
            tool = new BkendTool(null, this.getName());
        }
        Tool innerTool = new Tool();
        innerTool.setName(this.tool.getName());
        innerTool.setToolDesc(this.tool.getToolDesc());
        innerTool.setAttribute1(this.tool.getAttribute1());
        innerTool.setAttribute2(this.tool.getAttribute2());
        innerTool.setAttribute3(this.tool.getAttribute3());
        tool.setTool(innerTool);
        return tool;
    }
}
