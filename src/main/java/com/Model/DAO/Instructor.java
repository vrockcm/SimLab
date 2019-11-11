package com.Model.DAO;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Instructor {
    @Id private int id;

    private String name;


}
