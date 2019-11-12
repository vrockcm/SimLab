package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Material;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends CrudRepository <Material, Long> {

}
