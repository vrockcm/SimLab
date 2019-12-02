package com.SimLab.controller;

import com.SimLab.model.InstructionInfo;
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
    private CourseLabAssociationRepository courseLabAssociationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseAssociationRepository userCourseAssociationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LabService labService;

    @Autowired
    private MaterialRepository materialRepository;
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

    @Autowired
    private LabInstructionAssociationRepository labInstructionAssociationRepository;



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
                              @RequestParam(required = false) List<Integer> checkedStudents,
                              @RequestParam(required = false) List<Integer> checkedInstructors,
                              @RequestParam Integer courseId) {
        Course course = courseRepository.findByCourseId(courseId);
        course.setCourseName(courseName);
        course.setCourseDesc(courseDesc);
        courseRepository.save(course);
        userCourseAssociationRepository.removeByCourseId(course.getCourseId());
        List<UserCourseAssociation> userCourseAsses = new ArrayList<UserCourseAssociation>();
        if (checkedStudents != null) {
            for (Integer u : checkedStudents) {
                UserCourseAssociation uC = new UserCourseAssociation();
                uC.setCourseId(course.getCourseId());
                uC.setUserId(u);
                userCourseAsses.add(uC);
            }
        }
        if(checkedInstructors != null) {
            for (Integer u : checkedInstructors) {
                UserCourseAssociation uC = new UserCourseAssociation();
                uC.setCourseId(course.getCourseId());
                uC.setUserId(u);
                userCourseAsses.add(uC);
            }
        }
        userCourseAssociationRepository.saveAll(userCourseAsses);
        return "redirect:/instructor/index";
    }


    @RequestMapping(value = "/MakeCourse", method = RequestMethod.POST)
    public String createNewCourse(@RequestParam String courseName,
                                        @RequestParam String courseDesc,
                                        @RequestParam(value = "students", required = false) String[] students,
                                        @RequestParam(value = "instructors", required = false) String[] instructors,
                                        @RequestParam Integer UserId) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCourseDesc(courseDesc);
        courseRepository.save(course);
        List<UserCourseAssociation> userCourseAsses = new ArrayList<UserCourseAssociation>();
        UserCourseAssociation userCourse = new UserCourseAssociation();
        userCourse.setUserId(UserId);
        userCourse.setCourseId(course.getCourseId());
        userCourseAsses.add(userCourse);
        if (students != null) {
            for (String u : students){
                UserCourseAssociation uC = new UserCourseAssociation();
                uC.setCourseId(course.getCourseId());
                uC.setUserId(Integer.parseInt(u));
                userCourseAsses.add(uC);
            }
        }
        if (instructors != null) {
            for (String u : instructors){
                UserCourseAssociation uC = new UserCourseAssociation();
                uC.setCourseId(course.getCourseId());
                uC.setUserId(Integer.parseInt(u));
                userCourseAsses.add(uC);
            }
        }
        userCourseAssociationRepository.saveAll(userCourseAsses);
        return "redirect:/instructor/index";
    }



    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

    @ResponseBody
    @GetMapping("/loadCourses")
    public List<Course> loadCourse(@RequestParam String userid ){
        List<Course> userCourses = userCourseAssociationRepository.loadUserCourses(Integer.parseInt(userid));
        var toReturn = userCourses;
        return toReturn;
    }

    @ResponseBody
    @GetMapping(path = "/fetchCourseInfo", produces = "application/json; charset=UTF-8")
    public CourseInfo fetchCourseInfo(@RequestParam String courseId ){
        Course course = courseRepository.findByCourseId(Integer.parseInt(courseId));
        List<User> associatedUsers = userCourseAssociationRepository.findAllUsers(Integer.parseInt(courseId));
        List<User> students = new ArrayList<User>();
        List<User> instructors = new ArrayList<User>();
        List<User> allStudents = userService.findAllStudents();
        List<User> allInst = userService.findAllInstructors();
        for(User u: associatedUsers){
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
        courseInfo.setAllInstructors(allInst);
        courseInfo.setAllStudents(allStudents);

        var toReturn = courseInfo;
        return toReturn;

    }

    @ResponseBody
    @GetMapping("/loadLabs")
    public String loadLabs(@RequestParam String courseName){
        int courseId = courseRepository.getCourseId(courseName);
        List<Lab> associatedLabs = courseLabAssociationRepository.loadAssociatedLabs(courseId);
        String json = new Gson().toJson(associatedLabs);
        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/DeleteLab", method = RequestMethod.POST)
    public String deleteLab(@RequestParam String labId){
        Lab lab = labRepository.findByLabId(Integer.parseInt(labId));
        labRepository.delete(lab);
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/DeleteCourse", method = RequestMethod.POST)
    public String deleteCourse(@RequestParam String courseId){

        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/DuplicateLab", method = RequestMethod.POST)
    public String duplicateLab(@RequestParam String labId, @RequestParam String courseId){
        Lab lab = labRepository.findByLabId(Integer.parseInt(labId));
        Lab newLab = new Lab();
        newLab.setLabName(lab.getLabName()+"(copy)");
        newLab.setLabDesc(lab.getLabDesc());
        newLab.setTools(lab.getTools());
        newLab.setContainers(lab.getContainers());
        newLab.setSolutions(lab.getSolutions());
        labRepository.save(newLab);

        List<LabInstructionAssociation> labInsts = labInstructionAssociationRepository.findInstructionsByLabId(Integer.parseInt(labId));
        for(LabInstructionAssociation lI: labInsts){
            Instruction inst = instructionRepository.findByInstId(lI.getInstructionId());
            Instruction newInst = new Instruction(inst);
            instructionRepository.save(newInst);
            LabInstructionAssociation newLI = new LabInstructionAssociation();
            newLI.setLabId(newLab.getLabId());
            newLI.setInstructionId(newInst.getInstId());
            labInstructionAssociationRepository.save(newLI);
        }
        CourseLabAssociation courseLab = new CourseLabAssociation();
        courseLab.setLabId(newLab.getLabId());
        courseLab.setCourseId(Integer.parseInt(courseId));
        courseLabAssociationRepository.save(courseLab);
        return "";
    }

    @RequestMapping(value = "/MakeLab", method = RequestMethod.POST)
    public String createNewLab(@RequestParam String courseId,
                                @RequestParam String labName,
                                @RequestParam(required = false, defaultValue = "") String labDescription,
                                @RequestParam(required = false, defaultValue = "") List<String> Solutions,
                                @RequestParam(required = false, defaultValue = "") List<String> Containers,
                                @RequestParam(required = false, defaultValue = "") List<String> Tools,
                                @RequestParam String Instructions) {

        ObjectMapper mapper = new ObjectMapper();
        List<InstructionInfo> myObjects;
        try {
            myObjects = mapper.readValue(Instructions, mapper.getTypeFactory().constructCollectionType(List.class, InstructionInfo.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "redirect:/instructor/index";
    }

    private void addMaterialsToLab(Lab lab, List<String> toolNames, List<String> containerNames, List<String> solutionNames){
        Set<Tool> tools = new HashSet<Tool>();
        Set<Container> containers = new HashSet<Container>();
        Set<Solution> solutions = new HashSet<Solution>();
        for(String mat: toolNames){
            tools.add(toolRepository.findByName(mat));
        }
        for(String mat: containerNames){
            containers.add(containerRepository.findByName(mat));
        }
        for(String mat: solutionNames){
            solutions.add(solutionRepository.findByName(mat));
        }
        labService.saveLab(lab, tools, containers, solutions);
    }

    private void addInstructionsToLab(Lab lab, List<InstructionInfo> instructions) {
//            for(InstructionInfo instInfo: instructions){
//                Instruction inst = new Instruction();
//                inst.setName(instInfo.getName());         //name of instruction
//                // set all materials as null first then check if there is a material by that name exists and if does then set to matX
//                inst.setContainer1(instInfo.getContainer1());
//                inst.setContainer2(instInfo.getContainer2());
//                inst.setTargetTemp(instInfo.getTargetTemp());
//                inst.setTargetVolume(instInfo.getTargetVolume());
//                String matName;
//                if(instInfo.getContainer1() != null) {
//                    matName = instMat1Names.get(i);
//                    if (!matName.equals(""))
//                        inst.setMaterial1Id(materialRepository.findByName(instMat1Names.get(i)).getId());
//                }
//                if(instMat2Names.size()!=0) {
//                    matName = instMat2Names.get(i);
//                    if (!matName.equals(""))
//                        inst.setMaterial2Id(materialRepository.findByName(instMat2Names.get(i)).getId());
//                }
//                if(instMat3Names.size()!=0) {
//                    matName = instMat3Names.get(i);
//                    if (!matName.equals(""))
//                        inst.setMaterial3Id(materialRepository.findByName(instMat3Names.get(i)).getId());
//                }
//                //set all parameters to null then check if a param was specified
//                inst.setParameter1(null);
//                inst.setParameter2(null);
//                inst.setParameter3(null);
//                String param;
//                if(instParam1Names.size()!=0) {
//                    param = instParam1Names.get(i);
//                    if (!param.equals("")) inst.setParameter1(param);
//                }
//                if(instParam2Names.size()!=0) {
//                    param = instParam2Names.get(i);
//                    if (!param.equals("")) inst.setParameter2(param);
//                }
//                if(instParam3Names.size()!=0) {
//                    param = instParam3Names.get(i);
//                    if (!param.equals("")) inst.setParameter3(param);
//                }
//                instructionRepository.save(inst);
//                LabInstructionAssociation labInst = new LabInstructionAssociation();
//                labInst.setLabId(lab.getLabId());
//                labInst.setInstructionId(inst.getInstId());
//                labInstructionAssociationRepository.save(labInst);
//            }
//    }
    }
}
