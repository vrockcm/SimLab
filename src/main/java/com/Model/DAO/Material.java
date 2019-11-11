package com.Model.DAO;

import javax.persistence.*;

@Entity
public class Material {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int materialId;

    @Column(name = "name")
    private String matetrialName;
    @Column(name = "description")
    private String materialDesc;
    @Column(name = "attribute1")
    private String attribute1;
    @Column(name = "attribute2")
    private String attribute2;
    @Column(name = "attribute3")
    private String attribute3;

}
