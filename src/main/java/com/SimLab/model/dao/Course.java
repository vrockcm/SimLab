package com.SimLab.model.dao;

import javax.persistence.*;

@Entity
public class Course {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int courseId;
    @Column(name = "name")
    private String courseName;
    @Column(name = "description")
    private String courseDesc;

}
