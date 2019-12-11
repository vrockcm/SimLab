package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;

import java.util.List;

public interface InstructionBkend {
    public int verify(List<Interaction> interactions, int startIndex);
}
