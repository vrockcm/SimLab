package com.SimLab.service;


import com.SimLab.model.dao.LabResult;
import com.SimLab.model.dao.Repository.LabResultRepository;
import com.SimLab.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("labResultService")
public class LabResultService {

    LabResultRepository labResultRepository;

    @Autowired
    public LabResultService(LabResultRepository labResultRepository){
        this.labResultRepository = labResultRepository;
    }

    public void save(LabResult  lr){ labResultRepository.save(lr);}

    public Set<LabResult> getLabResultsByUser(User user){ return user.getLabResults();}
}
