package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Lab;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabRepository extends CrudRepository <Lab, Long> {
    Lab findByLabId(int labId);
}
