package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.LabMaterialAssociation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabMaterialAssociationRepository extends CrudRepository <LabMaterialAssociation, Long> {
}
