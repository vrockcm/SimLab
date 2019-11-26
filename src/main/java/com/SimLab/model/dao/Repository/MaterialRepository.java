package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Material;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MaterialRepository extends MaterialBaseRepository<Material> {
}
