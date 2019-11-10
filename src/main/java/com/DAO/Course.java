package com.DAO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Course {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int courseId;

    private String courseName;
}
