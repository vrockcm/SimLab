package com.SimLab.service;

import com.SimLab.model.dao.Course;
import com.SimLab.model.dao.Lab;
import com.SimLab.model.dao.Repository.*;
import com.SimLab.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service("courseService")
public class CourseService {
    CourseRepository courseRepository;
    UserService userService;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    public Course findByCourseId(int courseId){ return courseRepository.findByCourseId(courseId); }
    public Course findByCourseName(String courseName){ return courseRepository.findByCourseName(courseName); }

    public void removeAllUserByCourseId(int courseId){
        Course course = courseRepository.findByCourseId(courseId);
        Set<User> users = course.getUsers();
        course.setUsers(new HashSet<User>());
        courseRepository.save(course);
    }

    public boolean deleteByCourseId(int courseId){
        Course course = findByCourseId(courseId);
        courseRepository.delete(course);
        return true;
    }

    public boolean editCourse(String courseName, String courseDesc, String[] students, String[] instructors){
        Course course = findByCourseName(courseName);
        course.setCourseName(courseName);
        course.setCourseDesc(courseDesc);

        Set<User> users = new HashSet<User>();
        if (students != null) {
            for (String u : students){
                User userObj = userService.findUserById(Integer.parseInt(u));
                users.add(userObj);
                userObj.getCourses().add(course);
            }
        }
        if (instructors != null) {
            for (String u : instructors){
                User userObj = userService.findUserById(Integer.parseInt(u));
                users.add(userObj);
                userObj.getCourses().add(course);
            }
        }
        course.setUsers(users);
        courseRepository.save(course);
        return true;
    }



    public boolean createCourse(String courseName, String courseDesc, String[] students, String[] instructors){
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCourseDesc(courseDesc);

        Set<User> users = new HashSet<User>();
        if (students != null) {
            for (String u : students){
                User userObj = userService.findUserById(Integer.parseInt(u));
                users.add(userObj);
                userObj.getCourses().add(course);
            }
        }
        if (instructors != null) {
            for (String u : instructors){
                User userObj = userService.findUserById(Integer.parseInt(u));
                users.add(userObj);
                userObj.getCourses().add(course);
            }
        }
        course.setUsers(users);
        courseRepository.save(course);
        return true;
    }

    public void addLab(Course course, Lab lab){
        course.getLabs().add(lab);
        courseRepository.save(course);
    }
    public void removeLab(Course course, Lab lab){
        course.getLabs().remove(lab);
        courseRepository.save(course);
    }








}
