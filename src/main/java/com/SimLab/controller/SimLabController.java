package com.SimLab.controller;

import com.SimLab.model.InstructionInfo;
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
        Course course = courseService.findByCourseId(courseId);
        course.setCourseName(courseName);
        course.setCourseDesc(courseDesc);
        List<User> allUsers = userService.findAll();
        Set<User> checkedUsers = new HashSet<User>();
        for(User u: allUsers){
            if(checkedStudents != null && checkedStudents.contains(u.getId())){
                checkedUsers.add(u);
            }if(checkedInstructors != null && checkedInstructors.contains(u.getId())){
                checkedUsers.add(u);
            }
        }
        course.setUsers(checkedUsers);

        courseRepository.save(course);
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
    @GetMapping("/loadCourses")
    public List<Course> loadCourse(@RequestParam String userid ){
        User user = userService.findUserById(Integer.parseInt(userid));
        Set<Course> userCourses = user.getCourses();
        for(Course c: userCourses){
            c.setUsers(null);
            Course dummyCourse = courseService.findByCourseId(c.getCourseId());
            dummyCourse.setCourseDesc("DDD");
        }
        List<Course> uCList = new ArrayList<Course>(userCourses);
        var toReturn = uCList;
        return toReturn;
    }

    @ResponseBody
    @GetMapping(path = "/fetchCourseInfo", produces = "application/json; charset=UTF-8")
    public CourseInfo fetchCourseInfo(@RequestParam String courseId ){
        Course course = courseRepository.findByCourseId(Integer.parseInt(courseId));
        Set<User> associatedUsers = courseService.findByCourseId(Integer.parseInt(courseId)).getUsers();
        List<User> students = new ArrayList<User>();
        List<User> instructors = new ArrayList<User>();
        List<User> allStudents = userService.findAllStudents();
        List<User> allInst = userService.findAllInstructors();
        for(User u: allStudents){
            u.setCourses(null);
        }
        for(User u: allInst){
            u.setCourses(null);
        }
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
        courseInfo.setAllInstructors(allInst);
        courseInfo.setAllStudents(allStudents);

        var toReturn = courseInfo;
        return toReturn;

    }

    @ResponseBody
    @GetMapping("/loadLabs")
    public String loadLabs(@RequestParam String courseName){
        Course course = courseService.findByCourseName(courseName);
        List<Lab> associatedLabs = new ArrayList<Lab>(course.getLabs());
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
        courseService.deleteByCourseId(Integer.parseInt(courseId));
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/DuplicateLab", method = RequestMethod.POST)
    public String duplicateLab(@RequestParam String labId, @RequestParam String courseId){
        Lab lab = labRepository.findByLabId(Integer.parseInt(labId));
        Lab newLab = new Lab();
        newLab.setLabName(lab.getLabName()+"(copy)");
        newLab.setLabDesc(lab.getLabDesc());
        labService.saveLab(newLab, lab.getTools(), lab.getContainers(), lab.getSolutions(), lab.getInstructions());

        courseService.addLab(courseService.findByCourseId(Integer.parseInt(courseId)), newLab);
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
            Lab lab = new Lab();

            Set<Solution> solutionSet = new HashSet<Solution>();
            Set<Container> containerSet = new HashSet<Container>();
            Set<Tool> toolSet = new HashSet<Tool>();
            Set<Instruction> instructionSet = new HashSet<Instruction>();
            List<Solution> allSolutions = labService.getAllSolutions();
            List<Container> allContainers = labService.getAllContainer();
            List<Tool> allTools = labService.getAllTools();
            for(Solution s: allSolutions){
                if(Solutions.contains("[\"" +s.getName() + "\"]")){
                    solutionSet.add(s);
                }
            }
            for(Container c: allContainers){
                if(Containers.contains("[\"" +c.getName() + "\"]")){
                    containerSet.add(c);
                }
            }
            for(Tool t: allTools){
                if(Tools.contains("[\"" +t.getName() + "\"]")){
                    toolSet.add(t);
                }
            }
            for(InstructionInfo iInfo: myObjects){
                Instruction i = new Instruction();
                i.setName(iInfo.getName());
                i.setContainer1(iInfo.getContainer1());
                i.setContainer2(iInfo.getContainer2());
                i.setTargetVolume(iInfo.getTargetVolume());
                i.setTargetTemp(iInfo.getTargetTemp());
                instructionSet.add(i);
            }

            lab.setLabName(labName);
            lab.setLabDesc(labDescription);
            labService.saveLab(lab, toolSet, containerSet, solutionSet, instructionSet);
            Course course = courseService.findByCourseId(Integer.parseInt(courseId));
            courseService.addLab(course, lab);
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
