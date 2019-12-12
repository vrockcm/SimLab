package com.SimLab.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "labResult")
public class LabResult{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "result_id")
    private int id;

    @Column(name = "LabId")
    private int labId;

    @Column(name = "StepNo")
    private int stepNo;

    @Column(name = "Verified")
    private int verified;




}
