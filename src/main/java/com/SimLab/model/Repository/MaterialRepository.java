package com.SimLab.model.Repository;

import com.SimLab.model.Material;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends CrudRepository <Material, Long> {

}
