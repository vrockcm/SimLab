package com.SimLab.model.Repository;

import com.SimLab.model.UserCourseAssociation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseAssociationRepository extends CrudRepository <UserCourseAssociation, Long> {
}
