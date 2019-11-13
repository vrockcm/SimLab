package com.SimLab.model.dao;

import javax.persistence.*;

@Entity
public class Lab {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int labId;
    @Column(name = "Name")
    private String labName;
    @Column(name = "Description")
    private String labDesc;
}
