package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity @IdClass(UserCourseKey.class)
@Table(name = "UserCourseAssociation")
public class UserCourseAssociation {
    @Id
    @Column(name = "UserId")
    private int userId;

    @Id
    @Column(name = "CourseId")
    private int courseId;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> user;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Course> course;

}
