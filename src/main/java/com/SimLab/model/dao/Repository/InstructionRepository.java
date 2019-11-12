package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.Instruction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends CrudRepository <Instruction, Long> {
}
