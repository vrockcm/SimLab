package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity @IdClass(UserCourseKey.class)
public class UserCourseAssociation {
    @Id
    @Column(name = "UserId")
    private int userId;

    @Id
    @Column(name = "CourseId")
    private int courseId;
}
