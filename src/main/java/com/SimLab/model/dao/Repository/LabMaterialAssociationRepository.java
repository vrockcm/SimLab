package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.LabMaterialAssociation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabMaterialAssociationRepository extends CrudRepository <LabMaterialAssociation, Long> {
    @Query("SELECT x FROM LabMaterialAssociation x WHERE x.labId=:labId")
    List<LabMaterialAssociation> findMaterialsByLabId(@Param("labId") int labId);
}
