package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Instruction {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int instId;

    @Column(name = "Name")
    private String name;
    @Column(name = "Material1Id")
    private Integer material1Id;
    @Column(name = "Material2Id")
    private Integer material2Id;
    @Column(name = "Material3Id")
    private Integer material3Id;
    @Column(name = "Parameter1")
    private String parameter1;
    @Column(name = "Parameter2")
    private String parameter2;
    @Column(name = "Parameter3")
    private String parameter3;

    public Instruction(Instruction inst){
        name = inst.getName();
        material1Id = inst.getMaterial1Id();
        material2Id = inst.getMaterial2Id();
        material3Id = inst.getMaterial3Id();
        parameter1 = inst.getParameter1();
        parameter2 = inst.getParameter2();
        parameter3 = inst.getParameter3();
    }
    public Instruction(){}
}
