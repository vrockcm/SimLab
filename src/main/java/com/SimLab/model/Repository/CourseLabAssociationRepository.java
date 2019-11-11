package com.SimLab.model.Repository;

import com.SimLab.model.CourseLabAssociation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseLabAssociationRepository extends CrudRepository <CourseLabAssociation, Long> {
}
