package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("SELECT x FROM Material x")
    public List<Material> findAllMaterials();

    @Query("SELECT x.materialId FROM Material x WHERE x.materialName = :matName")
    public List<Integer> findIdByName(@Param("matName") String matName);


}
