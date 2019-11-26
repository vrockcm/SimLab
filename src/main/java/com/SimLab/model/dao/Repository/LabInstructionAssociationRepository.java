package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.LabInstructionAssociation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabInstructionAssociationRepository extends CrudRepository <LabInstructionAssociation, Long> {
    @Query("SELECT x FROM LabInstructionAssociation x WHERE x.labId=:labId")
    List<LabInstructionAssociation> findInstructionsByLabId(@Param("labId") int labId);
}
