package com.example.istar.controller;

import com.alibaba.fastjson.JSON;
import com.example.istar.dto.PageDTO;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/test2")
    @ResponseBody
    public R<?> test2(PageDTO pageDTO) {
        return R.ok(userService.queryByPageHelper(pageDTO.getPageIndex(), pageDTO.getPageSize()));
    }

    @GetMapping("/test3")
    @ResponseBody
    public R<?> test3(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            stringBuilder.append(new String(bytes, 0, len));
        }

        return R.ok(JSON.parse(stringBuilder.toString()));
    }

}
