package com.Model.DAO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LabInstructionAssociation {
    @Id
    @Column(name = "LabId")
    private int labId;

    @Id
    @Column(name = "InstructionId")
    private int instructionId;

}
