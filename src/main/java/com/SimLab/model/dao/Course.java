package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "course")
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
