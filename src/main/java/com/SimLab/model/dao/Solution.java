package com.SimLab.model.dao;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
public class Solution extends Material{

    @Column(name = "Flamability")
    @Type(type="org.hibernate.type.ByteType")
    private byte flambility;

    @Column(name = "pH")
    private int pH;

    @Column(name = "Volume")
    private int volume;

    @Column(name = "Temperature")
    private int temperature;

    @Column(name = "HeatOfCombustion")
    private int headOfCombustion;

    @Column(name = "Radioactivity")
    private int radioactivity;

}