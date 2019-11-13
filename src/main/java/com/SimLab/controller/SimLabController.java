package com.SimLab.controller;

import com.SimLab.model.dao.Course;
import com.SimLab.model.dao.CourseLabAssociation;
import com.SimLab.model.dao.Lab;
import com.SimLab.model.dao.Repository.CourseLabAssociationRepository;
import com.SimLab.model.dao.Repository.CourseRepository;
import com.SimLab.model.dao.Repository.UserCourseAssociationRepository;
import com.SimLab.model.dao.User;
import com.SimLab.service.UserService;
import com.google.gson.Gson;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
public class SimLabController {

    @Autowired
    private CourseLabAssociationRepository courseLabAssociationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseAssociationRepository userCourseAssociationRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }


    @RequestMapping(value="/student/index", method = RequestMethod.GET)
    public ModelAndView studentHome(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("Email", user.getEmail());
        modelAndView.addObject("UserId", user.getId());
        modelAndView.addObject("Name", user.getName());
        modelAndView.setViewName("/student/index");
        return modelAndView;
    }


    @RequestMapping(value="/instructor/index", method = RequestMethod.GET)
    public ModelAndView instructorHome(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("Email", user.getEmail());
        modelAndView.addObject("UserId", user.getId());
        modelAndView.addObject("Name", user.getName());
        modelAndView.setViewName("/instructor/index");
        return modelAndView;
    }



    @RequestMapping(value = "/MakeCourse", method = RequestMethod.POST)
    public ModelAndView createNewCourse(@RequestParam String courseName,
                                        @RequestParam String courseDesc,
                                        @RequestParam List<String> checkedStudents,
                                        @RequestParam List<String> checkedInstructors) {
        ModelAndView modelAndView = new ModelAndView();
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCourseDesc(courseDesc);
        courseRepository.save(course);
        modelAndView.setViewName("/instructor/index");
        return modelAndView;
    }



    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

    @ResponseBody
    @GetMapping("/loadCourses")
    public List<String> loadCourse(@RequestParam String userid){
        List<Course> userCourses = userCourseAssociationRepository.loadUserCourses(Integer.parseInt(userid));
        List<String> userCoursesName = new ArrayList<String>();
        for (int x = 0; x< userCourses.size(); x++){
            userCoursesName.add(userCourses.get(x).getCourseName());
        }
        return userCoursesName;
    }

    @ResponseBody
    @GetMapping("/loadLabs")
    public String loadLabs(@RequestParam String courseName){
        int courseId = courseRepository.getCourseId(courseName);
        List<Lab> associatedLabs = courseLabAssociationRepository.loadAssociatedLabs(courseId);
        String json = new Gson().toJson(associatedLabs);
        return json;
    }


}
