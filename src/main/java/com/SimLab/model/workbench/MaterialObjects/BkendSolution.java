package com.SimLab.model.workbench.MaterialObjects;


import com.SimLab.model.dao.Solution;
import lombok.Data;

@Data
public class BkendSolution {


    private int flamability;
    private int heatOfCombustion;
    private String solutionName;
    private int pH;
    private int radioactivity;
    private int temperature;
    private double volume;

    public BkendSolution(Solution solution){
        flamability = solution.getFlamability();
        heatOfCombustion = solution.getHeatOfCombustion();
        solutionName = solution.getName();
        pH = solution.getPH();
        radioactivity = solution.getRadioactivity();
        temperature = solution.getTemperature();
        volume = solution.getVolume();
    }
    public BkendSolution(BkendSolution solution){
        flamability = solution.getFlamability();
        heatOfCombustion = solution.getHeatOfCombustion();
        solutionName = solution.getSolutionName();
        pH = solution.getPH();
        radioactivity = solution.getRadioactivity();
        temperature = solution.getTemperature();
        volume = solution.getVolume();
    }


}
