package com.SimLab.model.dao;

import javax.persistence.*;

@Entity
public class Instruction {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int labId;

    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String labDesc;
    @Column(name = "Material1Id")
    private int material1Id;
    @Column(name = "Material2Id")
    private int material2Id;
    @Column(name = "Material3Id")
    private int material3Id;
    @Column(name = "Parameter1")
    private String parameter1;
    @Column(name = "Parameter2")
    private String parameter2;
    @Column(name = "Parameter3")
    private String parameter3;
}
