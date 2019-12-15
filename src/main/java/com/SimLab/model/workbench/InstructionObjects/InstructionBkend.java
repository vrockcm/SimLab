package com.SimLab.model.workbench.InstructionObjects;

import com.SimLab.model.workbench.Interaction;

import java.util.List;


public interface InstructionBkend {

    public void verify(List<Interaction> interactions, int startIndex);

    public boolean getVerified();

    public String getMessage();

}
