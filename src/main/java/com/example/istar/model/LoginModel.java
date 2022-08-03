package com.example.istar.model;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.model.inter.RequestCheckerInterface;
import com.example.istar.utils.RegexTool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tian
 */
@Data
public class LoginModel implements RequestCheckerInterface {
    @ApiModelProperty(value = "登录用户名，手机号或邮箱")
    String data;
    @ApiModelProperty(value = "登录验证码")
    String code;

    @Override
    public boolean isCorrect() {
        return ObjectUtil.isAllNotEmpty(data, code) && RegexTool.isEmailOrMobile(data);
    }
}
