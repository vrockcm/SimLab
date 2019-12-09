package com.SimLab.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WorkbenchController {

    @RequestMapping(value = "/SetupWorkbench", method = RequestMethod.POST)
    public void setupWorkbench(){

    }

    @RequestMapping(value = "/interact", method = RequestMethod.POST)
    public String interact(){
        return "";
    }


}
