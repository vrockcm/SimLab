package com.SimLab.controller;


import com.SimLab.model.dao.Instruction;
import com.SimLab.model.dao.Lab;
import com.SimLab.model.workbench.WorkbenchBkend;
import com.SimLab.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;

@Controller
public class WorkbenchController {
    @Autowired
    private LabService labService;


    private WorkbenchBkend workbenchBkend;

    @GetMapping(value="/workbench")
    public ModelAndView workbench(@RequestParam String labId){
        ModelAndView modelAndView = new ModelAndView();
        Lab lab = labService.findByLabId(Integer.parseInt(labId));
        Set<Instruction> instructions = lab.getInstructions();
        Instruction i = null;
        for(Instruction inst : instructions){
            if(inst.getStepNumber()==1){
                i = inst;
                break;
            }
        }
        modelAndView.setViewName("workbench");
        modelAndView.addObject("lab", lab);
        modelAndView.addObject("solutions", lab.getSolutions());
        modelAndView.addObject("containers", lab.getContainers());
        modelAndView.addObject("tools", lab.getTools());
        modelAndView.addObject("header", i.toString());
        workbenchBkend = new WorkbenchBkend(instructions);

        return modelAndView;
    }


    @RequestMapping(value = "/interact", method = RequestMethod.POST)
    public String interact(){
        return "";
    }

    //Routing for moveToWorkBench ajax call.
    @ResponseBody
    @RequestMapping(value = "/moveToWorkBench", method = RequestMethod.POST)
    public String moveToWorkBench(@RequestParam String materialName){

        return "";
    }

    //Routing for moveToInventory ajax call.
    @ResponseBody
    @RequestMapping(value = "/moveToInventory", method = RequestMethod.POST)
    public String moveToInventory(@RequestParam String materialName){

        return "";
    }

    //Routing for pour ajax call.
    @ResponseBody
    @RequestMapping(value = "/pour", method = RequestMethod.POST)
    public String pour(@RequestParam String beaker1, @RequestParam String beaker2, @RequestParam String amount){

        return "";
    }

    //Routing for mix ajax call.
    @ResponseBody
    @RequestMapping(value = "/mix", method = RequestMethod.POST)
    public String mix(@RequestParam String beaker1, @RequestParam String time){

        return "";
    }

    //Routing for heat ajax call.
    @ResponseBody
    @RequestMapping(value = "/heat", method = RequestMethod.POST)
    public String heat(@RequestParam String beaker1, @RequestParam String temp){

        return "";
    }



}
