package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "container")
public class Container extends Material{

    @Column(name = "SolutionpH")
    private int pH;

    @Column(name = "SolutionTemp")
    private int temperature;

    @Column(name = "Capacity")
    private int capacity;




}
