package com.SimLab.controller;

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
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private LabRepository labRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private InstructionRepository instructionRepository;

    @Autowired
    private LabMaterialAssociationRepository labMaterialAssociationRepository;

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
        instructorsObjects.remove(user);
        modelAndView.addObject("students", studentsObjects);
        modelAndView.addObject("instructors", instructorsObjects);
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
    public String deleteLab(@RequestParam String labId, @RequestParam String courseName){
        Lab lab = labRepository.findByLabId(Integer.parseInt(labId));
        labRepository.delete(lab);
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/DuplicateLab", method = RequestMethod.POST)
    public String duplicateLab(@RequestParam String labId, @RequestParam String courseName){
        Lab lab = labRepository.findByLabId(Integer.parseInt(labId));
        Lab newLab = new Lab();
        newLab.setLabName(lab.getLabName()+"(copy)");
        newLab.setLabDesc(lab.getLabDesc());
        labRepository.save(newLab);
        List<LabMaterialAssociation> labMats = labMaterialAssociationRepository.findMaterialsByLabId(Integer.parseInt(labId));
        List<LabInstructionAssociation> labInsts = labInstructionAssociationRepository.findInstructionsByLabId(Integer.parseInt(labId));
        for(LabMaterialAssociation lM: labMats){
            LabMaterialAssociation newLM = new LabMaterialAssociation();
            newLM.setLabId(newLab.getLabId());
            newLM.setMaterialId(lM.getMaterialId());
            labMaterialAssociationRepository.save(newLM);
        }
        for(LabInstructionAssociation lI: labInsts){
            Instruction inst = instructionRepository.findByInstId(lI.getInstructionId());
            Instruction newInst = new Instruction(inst);
            instructionRepository.save(newInst);
            LabInstructionAssociation newLI = new LabInstructionAssociation();
            newLI.setLabId(newLab.getLabId());
            newLI.setInstructionId(newInst.getInstId());
            labInstructionAssociationRepository.save(newLI);
        }
        int courseId = courseRepository.getCourseId(courseName);
        CourseLabAssociation courseLab = new CourseLabAssociation();
        courseLab.setLabId(newLab.getLabId());
        courseLab.setCourseId(courseId);
        courseLabAssociationRepository.save(courseLab);
        return "";
    }

    @RequestMapping(value = "/MakeLab", method = RequestMethod.POST)
    public String createNewLab(@RequestParam String courseId,
                                        @RequestParam String labName,
                                        @RequestParam(required = false, defaultValue = "") String labDescription,
                                        @RequestParam(required = false, defaultValue = "") List<String> materialNames,
                                        @RequestParam(required = false, defaultValue = "") List<String> instructionNames,
                                        @RequestParam(required = false, defaultValue = "") List<String> instMat1Names,
                                        @RequestParam(required = false, defaultValue = "") List<String> instMat2Names,
                                        @RequestParam(required = false, defaultValue = "") List<String> instMat3Names,
                                        @RequestParam(required = false, defaultValue = "") List<String> instParam1Names,
                                        @RequestParam(required = false, defaultValue = "") List<String> instParam2Names,
                                        @RequestParam(required = false, defaultValue = "") List<String> instParam3Names) {

        ModelAndView modelAndView = new ModelAndView();
        Lab lab = new Lab();
        lab.setLabName(labName);
        lab.setLabDesc(labDescription);
        labRepository.save(lab);
        addMaterialsToLab(lab, materialNames);
        addInstructionsToLab(lab, instructionNames, instMat1Names, instMat2Names, instMat3Names,
                instParam1Names, instParam2Names, instParam3Names);
        CourseLabAssociation courseLab = new CourseLabAssociation();
        courseLab.setCourseId(Integer.parseInt(courseId));
        courseLab.setLabId(lab.getLabId());
        courseLabAssociationRepository.save(courseLab);
        modelAndView.setViewName("/instructor/index");
        return "redirect:/instructor/index";
    }

    private void addMaterialsToLab(Lab lab, List<String> materials){
        for(String mat: materials){
            List<Integer> id = materialRepository.findIdByName(mat);
            LabMaterialAssociation labMat = new LabMaterialAssociation();
            labMat.setLabId(lab.getLabId());
            labMat.setMaterialId(id.get(0));
            labMaterialAssociationRepository.save(labMat);
        }
    }

    private void addInstructionsToLab(Lab lab, List<String> instNames, List<String> instMat1Names,
                                      List<String> instMat2Names, List<String> instMat3Names,
                                      List<String> instParam1Names, List<String> instParam2Names, List<String> instParam3Names){
            for(int i=0; i<instNames.size();i++){
                Instruction inst = new Instruction();
                inst.setName(instNames.get(i));         //name of instruction
                // set all materials as null first then check if there is a material by that name exists and if does then set to matX
                inst.setMaterial1Id(null);
                inst.setMaterial2Id(null);
                inst.setMaterial3Id(null);
                String matName;
                if(instMat1Names.size()!=0) {
                    matName = instMat1Names.get(i);
                    if (!matName.equals(""))
                        inst.setMaterial1Id(materialRepository.findIdByName(instMat1Names.get(i)).get(0));
                }
                if(instMat2Names.size()!=0) {
                    matName = instMat2Names.get(i);
                    if (!matName.equals(""))
                        inst.setMaterial2Id(materialRepository.findIdByName(instMat2Names.get(i)).get(0));
                }
                if(instMat3Names.size()!=0) {
                    matName = instMat3Names.get(i);
                    if (!matName.equals(""))
                        inst.setMaterial3Id(materialRepository.findIdByName(instMat3Names.get(i)).get(0));
                }
                //set all parameters to null then check if a param was specified
                inst.setParameter1(null);
                inst.setParameter2(null);
                inst.setParameter3(null);
                String param;
                if(instParam1Names.size()!=0) {
                    param = instParam1Names.get(i);
                    if (!param.equals("")) inst.setParameter1(param);
                }
                if(instParam2Names.size()!=0) {
                    param = instParam2Names.get(i);
                    if (!param.equals("")) inst.setParameter2(param);
                }
                if(instParam3Names.size()!=0) {
                    param = instParam3Names.get(i);
                    if (!param.equals("")) inst.setParameter3(param);
                }
                instructionRepository.save(inst);
                LabInstructionAssociation labInst = new LabInstructionAssociation();
                labInst.setLabId(lab.getLabId());
                labInst.setInstructionId(inst.getInstId());
                labInstructionAssociationRepository.save(labInst);
            }
    }
}
