package com.SimLab.controller;


import com.SimLab.model.Configs.CustomSuccessHandler;
import com.SimLab.model.dao.*;
import com.SimLab.model.workbench.InstructionObjects.InstructionBkend;
import com.SimLab.model.workbench.MaterialObjects.BkendContainer;
import com.SimLab.model.workbench.MaterialObjects.BkendTool;
import com.SimLab.model.workbench.WorkbenchBkend;
import com.SimLab.service.LabResultService;
import com.SimLab.service.LabService;
import com.SimLab.service.UserService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class WorkbenchController {

    @Autowired
    private LabResultService labResultService;
    @Autowired
    private LabService labService;
    @Autowired
    private UserService userService;



    private WorkbenchBkend workbenchBkend;

    @GetMapping(value="/workbench")
    public ModelAndView workbench(@RequestParam String labId){
        ModelAndView modelAndView = new ModelAndView();
        Lab lab = labService.findByLabId(Integer.parseInt(labId));
        List<String> instruct = new ArrayList<>();
        List<Instruction> instructions = new ArrayList(lab.getInstructions());
        instructions.sort(Comparator.comparing(e -> e.getStepNumber()));
        for(Instruction inst : instructions){
            instruct.add(inst.toString());
        }
        modelAndView.setViewName("workbench");
        modelAndView.addObject("lab", lab);
        modelAndView.addObject("solutions", lab.getSolutions());
        modelAndView.addObject("containers", lab.getContainers());
        modelAndView.addObject("tools", lab.getTools());
        modelAndView.addObject("instructions", instruct);
        workbenchBkend = new WorkbenchBkend(lab);

        workbenchBkend.addMaterial("HCl");
        workbenchBkend.addMaterial("Beaker");
        workbenchBkend.addMaterial("NaCl");
        workbenchBkend.addMaterial("Beaker");
        workbenchBkend.addMaterial("Beaker");
        workbenchBkend.interact("Pour", "Beaker1", "Beaker2", null, 10, 0);
        workbenchBkend.interact("Pour", "Beaker3", "Beaker4", null, 5, 0);
        workbenchBkend.interact("Pour", "Beaker2", "Beaker4", null, 5, 0);
        workbenchBkend.interact("Pour", "Beaker4", "Beaker5", null, 6, 0);


        return modelAndView;
    }


    @RequestMapping(value = "/interact", method = RequestMethod.POST)
    public String interact(){
        return "";
    }

    //Routing for moveToWorkBench ajax call.
    @ResponseBody
    @RequestMapping(value = "/moveToWorkBench", method = RequestMethod.POST)
    public BkendContainer moveToWorkBench(@RequestParam String materialName){
        BkendContainer container = workbenchBkend.addMaterial(materialName);
        var c = container;
        return c;
    }

    @ResponseBody
    @RequestMapping(value = "/moveToolToWorkBench", method = RequestMethod.POST)
    public BkendTool moveToolToWorkBench(@RequestParam String materialName){
        BkendTool tool = workbenchBkend.addTool(materialName);
        var c = tool;
        return c;
    }

    //Routing for moveToInventory ajax call.
    @ResponseBody
    @RequestMapping(value = "/moveToInventory", method = RequestMethod.POST)
    public String moveToInventory(@RequestParam String materialName){
        workbenchBkend.removeMaterial(materialName);
        return "";
    }

    //Routing for pour ajax call.
    @ResponseBody
    @RequestMapping(value = "/pour", method = RequestMethod.POST)
    public List<BkendContainer> pour(@RequestParam String beaker1, @RequestParam String beaker2, @RequestParam String amount){
        workbenchBkend.interact("Pour",beaker1,beaker2,null,Integer.parseInt(amount),0);
        List<BkendContainer> containers = new ArrayList<BkendContainer>();
        containers.add(workbenchBkend.getContainer(beaker1));
        containers.add(workbenchBkend.getContainer(beaker2));
        return containers;
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

    //Routing for finishLab ajax call.
    @ResponseBody
    @RequestMapping(value = "/finishLab", method = RequestMethod.POST)
    public String finishLab(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<String> contNames = labService.getAllContainer().stream().map(Container::getName).collect(Collectors.toList());Collectors.toList();

        List<InstructionBkend> results = workbenchBkend.verifyLab(contNames);
        int index = 1;
        for(InstructionBkend r: results){
            LabResult labResult = new LabResult();
            labResult.setLabId(workbenchBkend.getLab().getLabId());
            labResult.setStepNo(index);
            labResult.setVerified(1);
            if(!r.getVerified()) {
                labResult.setVerified(0);
                labResult.setMessage(r.getMessage());
            }
            user.getLabResults().add(labResult);
            index++;
        }
        userService.softSave(user);

//        if(request.isUserInRole("INSTRUCTOR"))
//            return "redirect:/instructor/index/";
//        else
//            return "redirect:/student/index/";
        return "";
    }

    @RequestMapping(value = "/cancelLab", method = RequestMethod.GET)
    public ModelAndView cancelLab(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        return new ModelAndView("/student/index");

    }



}
