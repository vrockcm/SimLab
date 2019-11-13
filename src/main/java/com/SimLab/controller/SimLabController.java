package com.SimLab.controller;


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
        List<String> students = new ArrayList<>();
        for(int i=0;i<studentsObjects.size();i++){
            students.add(studentsObjects.get(i).getName()+" "+studentsObjects.get(i).getLastName());
        }
        List<User> instructorsObjects = userService.findAllInstructors();
        List<String> instructors = new ArrayList<>();
        for(int i=0;i<instructorsObjects.size();i++){
            if(user.getId() != instructorsObjects.get(i).getId())
                instructors.add(instructorsObjects.get(i).getName()+" "+instructorsObjects.get(i).getLastName());
        }

        //Test lab creation
        String labName = "Distillation";
        String labDesc = "Distill my dick";
        List<String> materialNames = Arrays.asList(new String[]{"h2o"});
        List<String> instructionNames = Arrays.asList(new String[]{"swirl","mix","measure"});
        List<String> instMat1Names = Arrays.asList(new String[]{"h2o", "h2o","h2o"});
        List<String> instMat2Names = Arrays.asList(new String[]{"", "h2o", ""});
        List<String> instMat3Names = Arrays.asList(new String[]{"", "", ""});
        List<String> instParam1Names = Arrays.asList(new String[]{"30", "20", "10"});
        List<String> instParam2Names = Arrays.asList(new String[]{"20", "", "5"});
        List<String> instParam3Names = Arrays.asList(new String[]{"10", "", ""});

        createNewLab("1", labName, labDesc, materialNames, instructionNames, instMat1Names,
                instMat2Names, instMat3Names, instParam1Names, instParam2Names, instParam3Names);

        modelAndView.addObject("students", students);
        modelAndView.addObject("instructors", instructors);
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
    public List<String> loadCourse(@RequestParam String userid ){
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

    @RequestMapping(value = "/MakeLab", method = RequestMethod.POST)
    public ModelAndView createNewLab(@RequestParam String courseId,
                                        @RequestParam String labName,
                                        @RequestParam String labDescription,
                                        @RequestParam List<String> materialNames,
                                        @RequestParam List<String> instructionNames,
                                        @RequestParam List<String> instMat1Names,
                                        @RequestParam List<String> instMat2Names,
                                        @RequestParam List<String> instMat3Names,
                                        @RequestParam List<String> instParam1Names,
                                        @RequestParam List<String> instParam2Names,
                                        @RequestParam List<String> instParam3Names) {

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
        return modelAndView;
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
                String matName = instMat1Names.get(i);
                if(!matName.equals("")) inst.setMaterial1Id(materialRepository.findIdByName(instMat1Names.get(i)).get(0));
                matName = instMat2Names.get(i);
                if(!matName.equals("")) inst.setMaterial2Id(materialRepository.findIdByName(instMat2Names.get(i)).get(0));
                matName = instMat3Names.get(i);
                if(!matName.equals("")) inst.setMaterial3Id(materialRepository.findIdByName(instMat3Names.get(i)).get(0));
                //set all parameters to null then check if a param was specified
                inst.setParameter1(null);
                inst.setParameter2(null);
                inst.setParameter3(null);
                String param = instParam1Names.get(i);
                if(!param.equals("")) inst.setParameter1(param);
                param = instParam2Names.get(i);
                if(!param.equals("")) inst.setParameter2(param);
                param = instParam3Names.get(i);
                if(!param.equals("")) inst.setParameter3(param);
                instructionRepository.save(inst);
                LabInstructionAssociation labInst = new LabInstructionAssociation();
                labInst.setLabId(lab.getLabId());
                labInst.setInstructionId(inst.getInstId());
                labInstructionAssociationRepository.save(labInst);
            }
    }
}
