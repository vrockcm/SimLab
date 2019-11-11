package com.SimLab.model.Repository;

import com.SimLab.model.LabMaterialAssociation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabMaterialAssociationRepository extends CrudRepository <LabMaterialAssociation, Long> {
}
