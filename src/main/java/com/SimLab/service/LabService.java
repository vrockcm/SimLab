package com.SimLab.service;

import com.SimLab.model.dao.*;
import com.SimLab.model.dao.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("labService")
public class LabService {
    LabRepository labRepository;
    ToolRepository toolRepository;
    ContainerRepository containerRepository;
    SolutionRepository solutionRepository;

    @Autowired
    public LabService(LabRepository labRepository,
            ToolRepository toolRepository,
            ContainerRepository containerRepository,
            SolutionRepository solutionRepository) {
        this.labRepository = labRepository;
        this.toolRepository = toolRepository;
        this.containerRepository = containerRepository;
        this.solutionRepository = solutionRepository;
    }

    public Lab findByLabId(int labId){return labRepository.findByLabId(labId);}

    public void saveLab(Lab lab, Set<Tool> tools, Set<Container> containers, Set<Solution> solutions, Set<Instruction> instructions){
        lab.setTools(tools);
        lab.setContainers(containers);
        lab.setSolutions(solutions);
        lab.setInstructions(instructions);
        labRepository.save(lab);
    }

    public List<Solution> getAllSolutions(){
        return solutionRepository.findAll();
    }
    public List<Tool> getAllTools(){ return toolRepository.findAll(); }

    public List<Container> getAllContainer(){ return containerRepository.findAll(); }

    public void setupMaterials(){
        Solution hcl = new Solution();
        hcl.setName("HCl");
        solutionRepository.save(hcl);
        Solution Poop = new Solution();
        Poop.setName("Poop");
        solutionRepository.save(Poop);
        Container c1 = new Container();
        c1.setName("Beaker");
        containerRepository.save(c1);
        Container c2 = new Container();
        c2.setName("Flask");
        containerRepository.save(c2);
    }




}
