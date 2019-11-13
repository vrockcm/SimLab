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
    @Query("SELECT y FROM UserCourseAssociation  x, Course y WHERE x.userId = :userId AND x.courseId = y.id")
    public List<Course> loadUserCourses(@Param("userId") int userId);
}