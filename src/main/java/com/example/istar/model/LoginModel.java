package com.example.istar.model;

import cn.hutool.core.util.ObjectUtil;
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
    String key;
    @ApiModelProperty(value = "登录验证码")
    String code;

    @Override
    public boolean isCorrect() {
        return ObjectUtil.isAllNotEmpty(key, code) && RegexTool.isEmailOrMobile(key);
    }
}
