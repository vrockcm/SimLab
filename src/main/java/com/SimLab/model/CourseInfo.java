package com.SimLab.model;

import com.SimLab.model.dao.Course;
import com.SimLab.model.dao.User;
import lombok.Data;

import java.util.List;

@Data
public class CourseInfo {
    private String courseName;
    private String courseDesc;
    private List<User> students;
    private List<User> instructors;
}
