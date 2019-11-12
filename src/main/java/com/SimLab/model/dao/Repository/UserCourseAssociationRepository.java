package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Course;
import com.SimLab.model.dao.UserCourseAssociation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCourseAssociationRepository extends CrudRepository <UserCourseAssociation, Long> {
    @Query("SELECT x.Name FROM Course x WHERE x.UserId = :userId")
    public List<Course> loadUserCourseNames(@Param("UserId") int userId);
}
