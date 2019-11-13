package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@Entity @IdClass(LabInstructionKey.class)
public class LabInstructionAssociation {
    @Id
    @Column(name = "LabId")
    private int labId;

    @Id
    @Column(name = "InstructionId")
    private int instructionId;

}
