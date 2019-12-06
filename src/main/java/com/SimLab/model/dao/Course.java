package com.SimLab.model.dao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "course_user", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "course_lab", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "lab_id"))
    Set<Lab> labs;
}
