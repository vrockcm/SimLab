package com.SimLab.model;

import com.SimLab.model.dao.LabResult;
import com.SimLab.model.dao.User;
import lombok.Data;

import java.util.List;

@Data
public class StudentResult {

    private User user;
    private boolean done;
    private double score;
    private List<LabResult> results;

    public StudentResult(User user, List<LabResult> results){
        this.user = user;
        this.results = results;
        done = false;
        if(results.size()>0){
            done = true;
        }
        double correct = 0;
        for(LabResult r: results){
            if(r.getVerified()==1) correct++;
        }
        score = correct/results.size();
    }

}
