package com.example.istar.dto.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.utils.RegexTool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CodeModel {
    @ApiModelProperty(value = "验证码发送地址，手机、邮箱")
    String data;
    @ApiModelProperty(value = "校验码")
    String checkId;

    @JSONField(serialize = false)
    public boolean isBadFormat() {
        return !RegexTool.isEmailOrMobile(data);
    }


}
