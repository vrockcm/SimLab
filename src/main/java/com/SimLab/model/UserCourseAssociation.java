package com.SimLab.model;

import javax.persistence.*;

@Entity
public class UserCourseAssociation {
    @Id
    @Column(name = "UserId")
    private int userId;

    @Id
    @Column(name = "CourseId")
    private int courseId;

}
