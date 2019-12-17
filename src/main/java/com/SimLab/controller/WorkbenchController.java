package com.SimLab.controller;


import com.SimLab.model.Configs.CustomSuccessHandler;
import com.SimLab.model.dao.*;
import com.SimLab.model.workbench.InstructionObjects.InstructionBkend;
import com.SimLab.model.workbench.InstructionTemplates;
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
import org.springframework.web.servlet.view.RedirectView;

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
    public List<BkendContainer> pour(@RequestParam String container1, @RequestParam String container2, @RequestParam String amount){
        workbenchBkend.interact(InstructionTemplates.POUR,container1,container2,null,Double.parseDouble(amount),0);
        List<BkendContainer> containers = new ArrayList<BkendContainer>();
        containers.add(workbenchBkend.getContainer(container1));
        containers.add(workbenchBkend.getContainer(container2));
        return containers;
    }

    //Routing for swirl ajax call.
    @ResponseBody
    @RequestMapping(value = "/mix", method = RequestMethod.POST)
    public BkendContainer mix(@RequestParam String container1){
        workbenchBkend.interact(InstructionTemplates.SWIRL, container1, null, null, 0,0);
        return workbenchBkend.getContainer(container1);
    }

    //Routing for draw out ajax call.
    @ResponseBody
    @RequestMapping(value = "/drawUp", method = RequestMethod.POST)
    public List<BkendContainer> drawUp(@RequestParam String container1, @RequestParam String container2, @RequestParam double amount){
        workbenchBkend.interact(InstructionTemplates.DRAWUP, container1, container2, null, amount,0);
        List<BkendContainer> containers = new ArrayList<BkendContainer>();
        containers.add(workbenchBkend.getContainer(container1));
        containers.add(workbenchBkend.getContainer(container2));
        return containers;
    }

    @ResponseBody
    @RequestMapping(value = "/release", method = RequestMethod.POST)
    public List<BkendContainer> release(@RequestParam String container1, @RequestParam String container2, @RequestParam int amount){
        workbenchBkend.interact(InstructionTemplates.RELEASE, container1, container2, null, amount,0);
        List<BkendContainer> containers = new ArrayList<BkendContainer>();
        containers.add(workbenchBkend.getContainer(container1));
        containers.add(workbenchBkend.getContainer(container2));
        return containers;
    }


    //Routing for heat ajax call.
    @ResponseBody
    @RequestMapping(value = "/heat", method = RequestMethod.POST)
    public BkendContainer heat(@RequestParam String container1, @RequestParam double temp){
        workbenchBkend.interact(InstructionTemplates.HEAT, container1, null, null, 0, temp);
        BkendContainer cont = workbenchBkend.getContainer(container1);
        cont.update();
        return cont;
    }

    @ResponseBody
    @RequestMapping(value = "/cool", method = RequestMethod.POST)
    public BkendContainer cool(@RequestParam String container1, @RequestParam double temp){
        workbenchBkend.interact(InstructionTemplates.COOL, container1, null, null, 0, temp);
        BkendContainer cont = workbenchBkend.getContainer(container1);
        cont.update();
        return cont;
    }

    //Routing for finishLab ajax call.
    @ResponseBody
    @RequestMapping(value = "/finishLab", method = RequestMethod.POST)
    public String finishLab(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<String> contNames = labService.getAllContainer().stream().map(Container::getName).collect(Collectors.toList());

        List<InstructionBkend> results = workbenchBkend.verifyLab(contNames);
        int index = 1;
        for(InstructionBkend r: results){
            if(r!=null) {
                LabResult labResult = new LabResult();
                labResult.setLabId(workbenchBkend.getLab().getLabId());
                labResult.setStepNo(index);
                labResult.setStepName(workbenchBkend.getLab().getInstructionByStepNo(index).toString());
                labResult.setVerified(1);
                if (!r.getVerified()) {
                    labResult.setVerified(0);
                    labResult.setMessage(r.getMessage());
                }
                user.getLabResults().add(labResult);
                index++;
            }
        }
        userService.softSave(user);

        return cancelLab();

    }

    @ResponseBody
    @GetMapping(value = "/cancelLab")
    public String cancelLab(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Set<Role> role = user.getRoles();
        String name = "";
        for(Role r: role){
            name += r.getRole();
        }

        if(name.equals("INSTRUCTOR"))
            return "/instructor/index/";
        else
            return "/student/index/";


    }



}
