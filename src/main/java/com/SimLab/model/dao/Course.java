package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private int courseId;

    @Column(name = "name",unique = true)
    private String courseName;
    @Column(name = "Description")
    private String courseDesc;

    @ManyToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<UserCourseAssociation> userCourse;
}
