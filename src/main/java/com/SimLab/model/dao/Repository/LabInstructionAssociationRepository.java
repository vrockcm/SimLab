package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.LabInstructionAssociation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabInstructionAssociationRepository extends CrudRepository <LabInstructionAssociation, Long> {
}
