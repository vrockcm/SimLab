package com.SimLab.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Student {
    @Id private int id;

    private String name;

}
