package com.SimLab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity @IdClass(CourseLabKey.class)
public class CourseLabAssociation {
    @Id
    @Column(name = "CourseId")
    private int courseId;
    @Id
    @Column(name = "LabId")
    private int labId;
    
}
