package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "container")
public class Container{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "container_id")
    private int id;

    @Column(name = "container_name")
    private String name;

    @Column(name = "Capacity")
    private int capacity;

    @Column(name = "Weight")
    private double weight;



}
