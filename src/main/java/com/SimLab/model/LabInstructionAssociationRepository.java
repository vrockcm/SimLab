package com.SimLab.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabInstructionAssociationRepository extends CrudRepository <LabInstructionAssociation, Long> {
}
