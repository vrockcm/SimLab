package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MaterialBaseRepository<T extends Material> extends JpaRepository<T, Long> {

     @Query("select u from #{#entityName} as u where u.name = ?1 ")
     public T findByName(String name);

}
