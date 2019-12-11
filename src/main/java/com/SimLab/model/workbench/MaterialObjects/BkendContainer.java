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

    //for cloning
    public BkendContainer(BkendContainer c){
        name = c.getName();

    }

    public BkendContainer(String name, BkendSolution solution, int capacity){
        solutions = new ArrayList<BkendSolution>();
        this.name = name;
        if(solution != null) solutions.add(solution);
        this.capacity = capacity;
    }

    public double getCumulativeVolume(){
        int vol = 0;
        for(BkendSolution sol: solutions){
            vol+=sol.getVolume();
        }
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
        return rounded;
    }
    public int getCumulativeFlamability(){
        int flam = 0;
        for(BkendSolution sol: solutions){
            if(sol.getFlamability()>flam) flam = sol.getFlamability();
        }
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
