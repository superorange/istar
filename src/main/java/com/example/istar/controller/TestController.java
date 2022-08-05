package com.example.istar.controller;

import com.alibaba.fastjson.JSON;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.Res;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author tian
 */
@Controller
public class TestController {
    @Resource
    private UserServiceImpl userService;

    @GetMapping("/test")
    public String test() {
        return "redirect:/login.html";
    }

    @GetMapping("/test1")
    @ResponseBody
    public String test1() {
        return "redirect:/login.html";
    }

    @GetMapping("/test3")
    @ResponseBody
    public Res<?> test3(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            stringBuilder.append(new String(bytes, 0, len));
        }

        return Res.ok(JSON.parse(stringBuilder.toString()));
    }

}
