package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Tool extends Material{

    @Column(name = "Description")
    private String toolDesc;
    @Column(name = "Attribute1")
    private String attribute1;
    @Column(name = "Attribute2")
    private String attribute2;
    @Column(name = "Attribute3")
    private String attribute3;


}
