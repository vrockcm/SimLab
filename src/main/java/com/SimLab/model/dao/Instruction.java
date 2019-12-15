package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class Instruction {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int instId;

    @Column(name = "Name")
    private String name;
    @Column(name = "Container1Name")
    private String Container1;
    @Column(name = "Container2Name")
    private String Container2;
    @Column(name = "TargetTemp")
    private int targetTemp;
    @Column(name = "TargetVolume")
    private int targetVolume;

    @Column(name = "step_number")
    private int stepNumber;




    public Instruction(Instruction inst){
        name = inst.getName();
        Container1 = inst.getContainer1();
        Container2 = inst.getContainer2();
        targetTemp = inst.getTargetTemp();
        targetVolume = inst.getTargetVolume();
    }

    @Override
    public String toString(){
        String str = "";
        if(getName().matches("Pour|Transfer"))
            str = "Step "+getStepNumber()+": "+getName()+" "+getTargetVolume()+" mL of "+getContainer1()+" into "+ getContainer2();
        else if(getName().matches("Weigh|Swirl|Rinse"))
            str = "Step "+getStepNumber()+": "+getName()+" "+getContainer1();
        else if(getName().matches("Heat|Cool"))
            str = "Step "+getStepNumber()+": "+getName()+" "+getContainer1()+" to "+ getTargetTemp()+ " °C";
        else
            str = "Step "+getStepNumber()+": "+getName()+" "+ getTargetVolume()+ " of "+ getContainer1();
        return str;
    }
    public Instruction(){}
}
