package com.SimLab.service;


import com.SimLab.model.dao.Course;
import com.SimLab.model.dao.Repository.CourseRepository;
import com.SimLab.model.dao.Role;
import com.SimLab.model.dao.Repository.RoleRepository;
import com.SimLab.model.dao.User;
import com.SimLab.model.dao.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userService")
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User findUserById(int id){ return userRepository.findById(id);}


    public List<User> findAllInstructors(){ return userRepository.findAllUsingRole("INSTRUCTOR");}
    public List<User> findAllStudents(){ return userRepository.findAllUsingRole("STUDENT");}
    public int findRoleIdByUserId(int userId){ return userRepository.findRoleIdByUserID(userId); }

    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole;
        if (user.isDefault_role_instructor()) {
            userRole = roleRepository.findByRole("INSTRUCTOR");
        }
        else{
            userRole = roleRepository.findByRole("STUDENT");
        }
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    public void removeUser(User user){
        userRepository.delete(user);
    }
    public void softSave(User user){
        userRepository.save(user);
    }

    public List<User> findAll(){ return userRepository.findAll(); }

    public List<Course> loadCoursesByUserId(int userId){
        User user = userRepository.findById(userId);
        Set<Course> courses = user.getCourses();
        return new ArrayList<Course>(courses);
    }


}