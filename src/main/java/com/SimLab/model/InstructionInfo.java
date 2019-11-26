package com.SimLab.model;

import com.SimLab.model.dao.Course;
import com.SimLab.model.dao.User;
import lombok.Data;

import java.util.List;

@Data
public class InstructionInfo {


    private String name;

    private String Container1;

    private String Container2;

    private int targetTemp;

    private int targetVolume;
}
