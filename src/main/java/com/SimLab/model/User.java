package com.SimLab.model;

import com.SimLab.model.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @Column(name = "id")
    private int id;

    @Id
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private Role role;



}
