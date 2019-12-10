package com.SimLab.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WorkbenchController {

    @RequestMapping(value = "/SetupWorkbench", method = RequestMethod.POST)
    public void setupWorkbench(){

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
