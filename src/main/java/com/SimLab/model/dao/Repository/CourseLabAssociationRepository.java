package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.CourseLabAssociation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseLabAssociationRepository extends CrudRepository <CourseLabAssociation, Long> {
}
