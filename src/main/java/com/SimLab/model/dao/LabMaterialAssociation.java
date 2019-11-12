package com.SimLab.model.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity @IdClass(LabMaterialKey.class)
public class LabMaterialAssociation {
    @Id
    @Column(name = "LabId")
    private int labId;

    @Id
    @Column(name = "MaterialId")
    private int materialId;

}
