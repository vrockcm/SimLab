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
    public Instruction(){}
}
