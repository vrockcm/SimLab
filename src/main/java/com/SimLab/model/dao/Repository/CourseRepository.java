package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository <Course, Long> {
}
