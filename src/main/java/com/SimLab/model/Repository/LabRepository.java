package com.SimLab.model.Repository;

import com.SimLab.model.Lab;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabRepository extends CrudRepository <Lab, Long> {
}
