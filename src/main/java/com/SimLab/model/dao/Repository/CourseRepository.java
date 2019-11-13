package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends CrudRepository <Course, Long> {
    @Query("SELECT x.courseId FROM Course x WHERE x.courseName = :courseName")
    public int getCourseId(@Param("courseName") String courseName);

    @Query(value = "SELECT course.course_id, course.description, course.name " +
            "FROM user INNER JOIN user_course_association INNER JOIN course " +
            "WHERE user.user_id=user_course_association.user_id AND user_course_association.course_id=course.course_id " +
            "AND user.user_id='1'", nativeQuery=true)
    List<Course> loadUserCourses(@Param("userId") int userId);
}
