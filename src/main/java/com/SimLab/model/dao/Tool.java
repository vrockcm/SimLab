package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Tool{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "tool_id")
    private int id;

    @Column(name = "tool_name")
    private String name;

    @Column(name = "Description")
    private String toolDesc;
    @Column(name = "Attribute1")
    private String attribute1;
    @Column(name = "Attribute2")
    private String attribute2;
    @Column(name = "Attribute3")
    private String attribute3;


}
