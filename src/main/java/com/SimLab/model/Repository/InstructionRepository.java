package com.SimLab.model.Repository;

import com.SimLab.model.Instruction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends CrudRepository <Instruction, Long> {
}
