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
    @Column(name = "Published")
    private int published;
    @Column(name = "Time_limit")
    private int timeLimit;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lab_tool", joinColumns = @JoinColumn(name = "lab_id"), inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private Set<Tool> tools;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lab_container", joinColumns = @JoinColumn(name = "lab_id"), inverseJoinColumns = @JoinColumn(name = "container_id"))
    private Set<Container> containers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lab_solution", joinColumns = @JoinColumn(name = "lab_id"), inverseJoinColumns = @JoinColumn(name = "solution_id"))
    private Set<Solution> solutions;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "lab_instruction", joinColumns = @JoinColumn(name = "lab_id"), inverseJoinColumns = @JoinColumn(name = "Id"))
    private Set<Instruction> instructions;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "labs")
    private Set<Course> courses;

    private boolean completed;

}
