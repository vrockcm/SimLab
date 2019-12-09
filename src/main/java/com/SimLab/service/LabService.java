package com.SimLab.service;

import com.SimLab.model.dao.*;

import com.SimLab.model.dao.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    InstructionRepository instructionRepository;

    @Autowired
    public LabService(LabRepository labRepository,
            ToolRepository toolRepository,
            ContainerRepository containerRepository,
            SolutionRepository solutionRepository,
              InstructionRepository instructionRepository,
              CourseService courseService) {
        this.labRepository = labRepository;
        this.toolRepository = toolRepository;
        this.containerRepository = containerRepository;
        this.solutionRepository = solutionRepository;
        this.instructionRepository = instructionRepository;
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
                          List<Instruction> myObjects){
        Lab lab = new Lab();
        populateLabWithInfo(lab, labName, labDescription, Solutions, Containers, Tools, myObjects);

        saveLab(lab, Integer.parseInt(courseId));
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
        saveLab(newLab, Integer.parseInt(courseId));
    }

    public void saveLab(Lab lab,int courseId){
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

    public void editLab(int labId,
                        String labName,
                        String labDescription,
                        List<String> Solutions,
                        List<String> Containers,
                        List<String> Tools,
                        List<Instruction> myObjects){
        Lab lab = findByLabId(labId);
        instructionRepository.deleteAll(lab.getInstructions());
        populateLabWithInfo(lab, labName, labDescription, Solutions, Containers, Tools, myObjects);
        labRepository.save(lab);

    }

    private void populateLabWithInfo(Lab lab,
                                     String labName,
                                     String labDescription,
                                     List<String> Solutions,
                                     List<String> Containers,
                                     List<String> Tools,
                                     List<Instruction> myObjects){
        lab.setLabName(labName);
        lab.setLabDesc(labDescription);

        Set<Solution> solutionSet = new HashSet<Solution>();
        Set<Container> containerSet = new HashSet<Container>();
        Set<Tool> toolSet = new HashSet<Tool>();
        Set<Instruction> instructionSet = new HashSet<Instruction>();
        List<Solution> allSolutions = getAllSolutions();
        List<Container> allContainers = getAllContainer();
        List<Tool> allTools = getAllTools();
        for(Solution s: allSolutions){
            if(Solutions != null && Solutions.contains(s.getName())){
                solutionSet.add(s);
            }
        }
        for(Container c: allContainers){
            if(Containers != null && Containers.contains(c.getName())){
                containerSet.add(c);
            }
        }
        for(Tool t: allTools){
            if(Tools != null && Tools.contains(t.getName())){
                toolSet.add(t);
            }
        }
        instructionSet.addAll(myObjects);
        lab.setTools(toolSet);
        lab.setContainers(containerSet);
        lab.setSolutions(solutionSet);
        lab.setInstructions(instructionSet);
    }



}
