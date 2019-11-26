package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Inheritance
public abstract class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "material_id")
    private int id;

    @Column(name = "material_name")
    private String name;
}
