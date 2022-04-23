package com.example.istar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tian
 */
@Controller
public class TestController {

    @GetMapping("/test")
    public String test   () {
        return "redirect:/login.html";
    }

    @GetMapping("/test1")
    @ResponseBody
    public String test1() {
        return "redirect:/login.html";
    }


}
