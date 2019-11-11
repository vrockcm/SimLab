package com.Model.DAO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CourseLabAssociation {
    @Id
    @Column(name = "CourseId")
    private int courseId;

    @Id
    @Column(name = "LabId")
    private int labId;
    
}
