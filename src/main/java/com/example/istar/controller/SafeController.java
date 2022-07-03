package com.example.istar.controller;

import com.example.istar.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/safe")
public class SafeController {
    @Resource
    private CodeController codeController;

    @PostMapping("/check")
    public R safeCheck() {
        //TODO 后面在写安全计划，现在统统不安全
        //1,如果安全，直接放行
        //2,不安全，需要滑块验证
        //不安全情况下，先判断redis里面是否有缓存

        return R.ok(codeController.genCode().getData());
    }

}
