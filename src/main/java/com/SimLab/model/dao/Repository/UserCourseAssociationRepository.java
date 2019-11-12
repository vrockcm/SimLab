package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.UserCourseAssociation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCourseAssociationRepository extends CrudRepository <UserCourseAssociation, Long> {
    @Query("SELECT x.courseName FROM UserCourseAssociation  x WHERE x.userEmail = :userEmail")
    public List<String> loadUserCourses(@Param("userEmail") String userEmail);
}
