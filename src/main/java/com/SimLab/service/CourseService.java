//package com.SimLab.service;
//
//
//import com.SimLab.model.dao.Course;
//import com.SimLab.model.dao.Repository.CourseRepository;
//import com.SimLab.model.dao.Role;
//import com.SimLab.model.dao.Repository.RoleRepository;
//import com.SimLab.model.dao.User;
//import com.SimLab.model.dao.Repository.UserRepository;
//import com.SimLab.model.dao.UserCourseAssociation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//
//@Service("courseService")
//public class CourseService {
//
//    private CourseRepository courseRepository;
//    private UserCourseAssociation userCourseAssociation;
//
//    @Autowired
//    public CourseService(CourseRepository courseRepository,
//                       UserCourseAssociation UCAssocRepository) {
//        this.courseRepository = courseRepository;
//        this.userCourseAssociation = UCAssocRepository;
//    }
//
////    public List<Course> getCoursesFromStudentId(int studentId){
////
////    }
//
//}