package com.SimLab.model.dao;

import javax.persistence.*;

@Entity
public class Material {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int materialId;

    @Column(name = "Name")
    private String materialName;
    @Column(name = "Description")
    private String materialDesc;
    @Column(name = "Attribute1")
    private String attribute1;
    @Column(name = "Attribute2")
    private String attribute2;
    @Column(name = "Attribute3")
    private String attribute3;

}
