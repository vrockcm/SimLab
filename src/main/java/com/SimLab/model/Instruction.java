package com.SimLab.model;

import javax.persistence.*;

@Entity
public class Instruction {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int labId;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String labDesc;
    @Column(name = "material1Id")
    private int material1Id;
    @Column(name = "material2Id")
    private int material2Id;
    @Column(name = "material3Id")
    private int material3Id;
    @Column(name = "parameter1")
    private String parameter1;
    @Column(name = "parameter2")
    private String parameter2;
    @Column(name = "parameter3")
    private String parameter3;
}
