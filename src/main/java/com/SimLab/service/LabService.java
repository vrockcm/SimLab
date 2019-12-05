package com.SimLab.service;

import com.SimLab.model.InstructionInfo;
import com.SimLab.model.dao.Container;
import com.SimLab.model.dao.Lab;
import com.SimLab.model.dao.Repository.*;
import com.SimLab.model.dao.Solution;
import com.SimLab.model.dao.Tool;
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

}
