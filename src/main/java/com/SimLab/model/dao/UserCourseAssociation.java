package com.SimLab.model.dao;

import javax.persistence.*;

@Entity @IdClass(UserCourseKey.class)
public class UserCourseAssociation {
    @Id
    @Column(name = "UserId")
    private int userId;

    @Id
    @Column(name = "CourseId")
    private int courseId;

    @Id
    @Column(name = "UserEmail")
    private String userEmail;

    @Id
    @Column(name = "CourseName")
    private String courseName;

}
