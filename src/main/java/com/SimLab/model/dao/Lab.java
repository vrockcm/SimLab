package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;

@Data
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
