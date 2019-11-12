package com.SimLab.model.dao;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Course {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private int courseId;

    @Column(name = "Name")
    private String courseName;
    @Column(name = "Description")
    private String courseDesc;

}
