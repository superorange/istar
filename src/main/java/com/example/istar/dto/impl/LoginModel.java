package com.example.istar.dto.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.utils.RegexTool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * @author tian
 */
@Data
public class LoginModel {
    @ApiModelProperty(value = "登录用户名，手机号或邮箱")
    String data;
    @ApiModelProperty(value = "登录验证码")
    String code;

    @JSONField(serialize = false)
    public boolean isBadFormat() {
        return ObjectUtil.isNull(data) || ObjectUtil.isNull(code) || !RegexTool.isEmailOrMobile(data);
    }


}
