package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name= "lab")
public class Lab {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "lab_id")
    private int labId;
    @Column(name = "Name")
    private String labName;
    @Column(name = "Description")
    private String labDesc;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "lab_tool", joinColumns = @JoinColumn(name = "lab_id"), inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private Set<Tool> tools;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "lab_container", joinColumns = @JoinColumn(name = "lab_id"), inverseJoinColumns = @JoinColumn(name = "container_id"))
    private Set<Container> containers;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "lab_solution", joinColumns = @JoinColumn(name = "lab_id"), inverseJoinColumns = @JoinColumn(name = "solution_id"))
    private Set<Solution> solutions;

}
