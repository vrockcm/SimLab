package com.SimLab.controller;

import com.SimLab.service.CourseService;
import com.SimLab.service.LabService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.var;
import com.SimLab.model.CourseInfo;
import com.SimLab.model.dao.*;
import com.SimLab.model.dao.Repository.*;
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
import java.util.*;


@Controller
public class SimLabController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LabService labService;

    @Autowired
    private CourseService courseService;


    @Autowired
    private LabRepository labRepository;
    @Autowired
    private ToolRepository toolRepository;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private InstructionRepository instructionRepository;



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

        List<User> studentsObjects = userService.findAllStudents();
        List<User> instructorsObjects = userService.findAllInstructors();
        List<Tool> toolObjects = toolRepository.findAll();
        List<Container> containerObjects = containerRepository.findAll();
        List<Solution> solutionObjects = solutionRepository.findAll();
        instructorsObjects.remove(user);
        List<Course> courses = new ArrayList<>(user.getCourses());
        modelAndView.addObject("courses", courses);
        modelAndView.addObject("students", studentsObjects);
        modelAndView.addObject("instructors", instructorsObjects);
        modelAndView.addObject("tools", toolObjects);
        modelAndView.addObject("containers", containerObjects);
        modelAndView.addObject("solutions", solutionObjects);

        modelAndView.setViewName("/instructor/index");

        return modelAndView;
    }


    @RequestMapping(value = "/EditCourse", method = RequestMethod.POST)
    public String editCourse(@RequestParam String courseName,
                             @RequestParam String courseDesc,
                             @RequestParam(value = "students", required = false) String[] students,
                             @RequestParam(value = "instructors", required = false) String[] instructors,
                             @RequestParam Integer courseId) {
        courseService.editCourse(courseName, courseDesc, students, instructors, courseId);
        return "redirect:/instructor/index";
    }




    @RequestMapping(value = "/MakeCourse", method = RequestMethod.POST)
    public String createNewCourse(@RequestParam String courseName,
                                        @RequestParam String courseDesc,
                                        @RequestParam(value = "students", required = false) String[] students,
                                        @RequestParam(value = "instructors", required = false) String[] instructors,
                                        @RequestParam Integer UserId) {

        String[] instructorPlusCreator;
        if(instructors != null){
            instructorPlusCreator = new String[instructors.length+1];
            for(int i=0;i<instructors.length;i++){
                instructorPlusCreator[i+1] = instructors[i];
            }
        }else{
            instructorPlusCreator = new String[1];
        }
        instructorPlusCreator[0] = Integer.toString(UserId);
        courseService.createCourse(courseName, courseDesc, students, instructorPlusCreator);

        return "redirect:/instructor/index";
    }


    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

    @ResponseBody
    @GetMapping(path = "/fetchCourseInfo", produces = "application/json; charset=UTF-8")
    public CourseInfo fetchCourseInfo(@RequestParam String courseId ){
        Course course = courseRepository.findByCourseId(Integer.parseInt(courseId));
        Set<User> associatedUsers = courseService.findByCourseId(Integer.parseInt(courseId)).getUsers();
        List<User> students = new ArrayList<User>();
        List<User> instructors = new ArrayList<User>();
        for(User u: associatedUsers){
            u.setCourses(null);
            int roleId = userService.findRoleIdByUserId(u.getId());
            if(roleId == 0){
                students.add(u);
            }else{
                instructors.add(u);
            }
        }
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setCourseName(course.getCourseName());
        courseInfo.setCourseDesc(course.getCourseDesc());
        courseInfo.setStudents(students);
        courseInfo.setInstructors(instructors);
        var toReturn = courseInfo;
        return toReturn;

    }

    @ResponseBody
    @GetMapping(path = "/fetchLabInfo", produces = "application/json; charset=UTF-8")
    public Lab fetchLabInfo(@RequestParam String labId){
        Lab lab = labService.findByLabId(Integer.parseInt(labId));
        lab.setCourses(null);
        var toReturn = lab;
        return toReturn;

    }

    @ResponseBody
    @GetMapping("/loadLabs")
    public String loadLabs(@RequestParam String courseName){
        Course course = courseService.findByCourseName(courseName);
        List<Lab> associatedLabs = new ArrayList<Lab>(course.getLabs());
        for(Lab l: associatedLabs){
            l.setCourses(null);
        }
        String json = new Gson().toJson(associatedLabs);
        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/DeleteLab", method = RequestMethod.POST)
    public String deleteLab(@RequestParam String labId, @RequestParam String courseId){
        labService.deleteByLabId(Integer.parseInt(labId), Integer.parseInt(courseId));
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/DeleteCourse", method = RequestMethod.POST)
    public String deleteCourse(@RequestParam String courseId){
        courseService.deleteByCourseId(Integer.parseInt(courseId));
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/DuplicateLab", method = RequestMethod.POST)
    public String duplicateLab(@RequestParam String labId, @RequestParam String courseId){
        Lab lab = labRepository.findByLabId(Integer.parseInt(labId));
        labService.duplicateLab(lab, courseId);

        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/MakeLab", method = RequestMethod.POST)
    public String createNewLab(@RequestParam String courseId,
                                @RequestParam String labName,
                                @RequestParam(required = false, defaultValue = "") String labDescription,
                                @RequestParam(required = false, defaultValue = "") List<String> Solutions,
                                @RequestParam(required = false, defaultValue = "") List<String> Containers,
                                @RequestParam(required = false, defaultValue = "") List<String> Tools,
                                @RequestParam String Instructions) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Instruction> myObjects = null;
            myObjects = mapper.readValue(Instructions, mapper.getTypeFactory().constructCollectionType(List.class, Instruction.class));
            labService.createLab(courseId, labName, labDescription, Solutions, Containers, Tools, myObjects);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/EditLab", method = RequestMethod.POST)
    public String editLab(@RequestParam String courseId,
                          @RequestParam String labId,
                          @RequestParam String labName,
                          @RequestParam(required = false, defaultValue = "") String labDescription,
                          @RequestParam(required = false, defaultValue = "") List<String> Solutions,
                          @RequestParam(required = false, defaultValue = "") List<String> Containers,
                          @RequestParam(required = false, defaultValue = "") List<String> Tools,
                          @RequestParam String Instructions) {
        deleteLab(labId, courseId);
        createNewLab(courseId, labName, labDescription, Solutions, Containers, Tools, Instructions);
        return "";//
    }
}
