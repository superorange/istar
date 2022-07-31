package com.example.istar.controller;

import com.example.istar.common.RedisConst;
import com.example.istar.dto.impl.CheckModel;
import com.example.istar.utils.CommonUtil;
import com.example.istar.utils.R;
import com.example.istar.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(tags = "动态安全预检查")
@RequestMapping("/safe")
public class SafeController {
    @Resource
    private CodeController codeController;
    @Resource
    private RedisUtil redisUtil;

    @PostMapping("/check")
    @ApiOperation(value = "检查动态检查")
    public R<CheckModel> safeCheck(@RequestParam String data) {
        //TODO 后面在写安全计划，现在统统不安全
        //1,如果安全，直接放行
        //2,不安全，需要滑块验证
        //不安全情况下，先判断redis里面是否有缓存
        CheckModel checkModel = new CheckModel(CommonUtil.generateTimeId());
        redisUtil.setCacheObject(RedisConst.REDIS_LOGIN_PRE_CHECK + data, checkModel.getCheckId());
        return R.ok(checkModel);
    }

}
