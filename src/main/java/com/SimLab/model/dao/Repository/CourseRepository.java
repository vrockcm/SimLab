package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("courseRepositry")
public interface CourseRepository extends CrudRepository <Course, Long> {
    @Query(value = "SELECT u FROM Course u WHERE u.gameId = :gameId")
    public List<Course> getCourse(@Param("CourseId") int courseId);
}
