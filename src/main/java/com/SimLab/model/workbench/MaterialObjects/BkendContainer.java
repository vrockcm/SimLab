package com.SimLab.model.workbench.MaterialObjects;

import com.SimLab.model.dao.Solution;
import lombok.Data;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

@Data
public class BkendContainer implements Cloneable{

    private String name;

    private List<BkendSolution> solutions;

    private int capacity;

    private int associatedStep;

    private double cumVolume;
    private double cumTemp;
    private double cumPH;
    private double cumFlam;
    private boolean swirled;


    //for cloning
    public BkendContainer(BkendContainer c){
        name = c.getName();

    }

    public BkendContainer(String name, BkendSolution solution, int capacity){
        solutions = new ArrayList<BkendSolution>();
        this.name = name;
        if(solution != null) solutions.add(solution);
        this.capacity = capacity;
        swirled = false;
    }

    public void addSolution(BkendSolution sol){
        swirled = false;
        solutions.add(sol);
    }

    public void update(){
        getCumulativeVolume();
        getCumulativeTemperature();
        getCumulativePh();
        getCumulativeFlamability();
    }

    public double getCumulativeVolume(){
        double vol = 0;
        for(BkendSolution sol: solutions){
            vol+=sol.getVolume();
        }
        cumVolume = vol;
        return vol;
    }
    public double getCumulativeTemperature(){
        double totalVol = getCumulativeVolume();
        double totalTemp = 0;
        for(BkendSolution sol: solutions){
            double percentage = sol.getVolume()/totalVol;
            double thisTemp = sol.getTemperature();
            totalTemp += (thisTemp*percentage);
        }
        BigDecimal bd = new BigDecimal(totalTemp);
        bd = bd.round(new MathContext(3));
        double rounded = bd.doubleValue();
        cumTemp = rounded;
        return rounded;
    }
    public int getCumulativeFlamability(){
        int flam = 0;
        for(BkendSolution sol: solutions){
            if(sol.getFlamability()>flam) flam = sol.getFlamability();
        }
        cumFlam = flam;
        return flam;
    }
    public double getCumulativePh(){
        double totalVol = getCumulativeVolume();
        double totalPh = 0;
        for(BkendSolution sol: solutions){
            double percentage = sol.getVolume()/totalVol;
            double thisPh = sol.getPH();
            totalPh += (thisPh*percentage);
        }
        BigDecimal bd = new BigDecimal(totalPh);
        bd = bd.round(new MathContext(2));
        double rounded = bd.doubleValue();
        cumPH = rounded;
        return rounded;
    }

    @Override
    public Object clone() {
        BkendContainer container;
        try {
            container = (BkendContainer) super.clone();
        } catch (CloneNotSupportedException e) {
            container = new BkendContainer(this.getName(),null,this.getCapacity());
        }
        List<BkendSolution> original = this.getSolutions();
        List<BkendSolution> clone = new ArrayList<BkendSolution>();
        for(BkendSolution s: original){
            clone.add((BkendSolution)s.clone());
        }
        container.setSolutions(clone);
        return container;
    }



}
