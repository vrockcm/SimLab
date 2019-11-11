package com.SimLab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LabMaterialAssociation {
    @Id
    @Column(name = "LabId")
    private int labId;

    @Id
    @Column(name = "MaterialId")
    private int materialId;

}
