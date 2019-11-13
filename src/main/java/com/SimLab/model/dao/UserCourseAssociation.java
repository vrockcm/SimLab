package com.SimLab.model.dao;

import javax.persistence.*;

@Entity
@Table(name = "userCourseAssociation")
@IdClass(UserCourseKey.class)
public class UserCourseAssociation {
    @Id
    @Column(name = "UserId")
    private int userId;

    @Id
    @Column(name = "CourseId")
    private int courseId;


}
