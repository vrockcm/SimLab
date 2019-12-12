package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Lab;
import com.SimLab.model.dao.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabResultRepository extends JpaRepository <LabResult, Long> {

}
