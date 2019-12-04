package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Course;
import com.SimLab.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findByCourseId(int courseId);

    Course findByCourseName(String courseName);

    Course removeByCourseName(String courseName);

    @Query("SELECT x.courseId FROM Course x WHERE x.courseName = :courseName")
    public int getCourseId(@Param("courseName") String courseName);

    @Query(value = "SELECT user.user_id, user.active, user.email, user.last_name, user.first_name, user.password FROM  user INNER JOIN course_user WHERE user.user_id = course_user.user_id AND course_user.course_id =:courseId", nativeQuery = true)
    public List<User> findAllUsers(@Param("courseId") int courseId);
}
