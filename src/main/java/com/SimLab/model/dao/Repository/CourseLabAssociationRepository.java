package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.CourseLabAssociation;
import com.SimLab.model.dao.Lab;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseLabAssociationRepository extends CrudRepository <CourseLabAssociation, Long> {
    @Query("SELECT y FROM CourseLabAssociation x, Lab y WHERE  x.labId = y.labId  AND x.courseId = :courseId")
    public List<Lab> loadAssociatedLabs(@Param("courseId") int courseId);
}
