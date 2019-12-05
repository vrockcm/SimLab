package com.SimLab.service;

import com.SimLab.model.InstructionInfo;
import com.SimLab.model.dao.*;

import com.SimLab.model.dao.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("labService")
public class LabService {
    CourseService courseService;
    LabRepository labRepository;
    ToolRepository toolRepository;
    ContainerRepository containerRepository;
    SolutionRepository solutionRepository;

    @Autowired
    public LabService(LabRepository labRepository,
            ToolRepository toolRepository,
            ContainerRepository containerRepository,
            SolutionRepository solutionRepository,
                      CourseService courseService) {
        this.labRepository = labRepository;
        this.toolRepository = toolRepository;
        this.containerRepository = containerRepository;
        this.solutionRepository = solutionRepository;
        this.courseService = courseService;
    }

    public Lab findByLabId(int labId){return labRepository.findByLabId(labId);}
    public List<Lab> findAll(){ return (List<Lab>)labRepository.findAll();}

    public void createLab(String courseId,
                          String labName,
                          String labDescription,
                          List<String> Solutions,
                          List<String> Containers,
                          List<String> Tools,
                          List<InstructionInfo> myObjects){
        Lab lab = new Lab();

        Set<Solution> solutionSet = new HashSet<Solution>();
        Set<Container> containerSet = new HashSet<Container>();
        Set<Tool> toolSet = new HashSet<Tool>();
        Set<Instruction> instructionSet = new HashSet<Instruction>();
        List<Solution> allSolutions = getAllSolutions();
        List<Container> allContainers = getAllContainer();
        List<Tool> allTools = getAllTools();
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
        saveLab(lab, toolSet, containerSet, solutionSet, instructionSet, Integer.parseInt(courseId));
    }

    public void duplicateLab(Lab lab, String courseId){
        Lab newLab = new Lab();
        newLab.setLabName(lab.getLabName()+"(copy)");
        newLab.setLabDesc(lab.getLabDesc());
        Set<Solution> solutions = new HashSet<>();
        Set<Container> containers = new HashSet<>();
        Set<Tool> tools = new HashSet<>();
        for(Solution s: lab.getSolutions()){
            solutions.add(s);
        }
        for(Container s: lab.getContainers()){
            containers.add(s);
        }
        for(Tool s: lab.getTools()){
            tools.add(s);
        }
        Set<Instruction> instructions = new HashSet<Instruction>();
        for(Instruction i: lab.getInstructions()){
            Instruction newI = new Instruction(i);
            instructions.add(newI);
        }
        saveLab(newLab, tools, containers, solutions, instructions, Integer.parseInt(courseId));
    }

    public void saveLab(Lab lab, Set<Tool> tools, Set<Container> containers, Set<Solution> solutions, Set<Instruction> instructions, int courseId){
        lab.setTools(tools);
        lab.setContainers(containers);
        lab.setSolutions(solutions);
        lab.setInstructions(instructions);
        labRepository.save(lab);
        courseService.addLab(courseService.findByCourseId(courseId), lab);
    }

    public List<Solution> getAllSolutions(){
        return solutionRepository.findAll();
    }
    public List<Tool> getAllTools(){ return toolRepository.findAll(); }

    public List<Container> getAllContainer(){ return containerRepository.findAll(); }

    public void deleteByLabId(int labId, int courseId){
        Lab lab = findByLabId(labId);
        courseService.removeLab(courseService.findByCourseId(courseId), lab);
        labRepository.delete(lab);

    }




}
