package com.SimLab.model.Repository;

import com.SimLab.model.LabInstructionAssociation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabInstructionAssociationRepository extends CrudRepository <LabInstructionAssociation, Long> {
}
