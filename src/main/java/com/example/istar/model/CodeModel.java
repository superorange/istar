package com.example.istar.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.model.inter.RequestCheckerInterface;
import com.example.istar.utils.RegexTool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tian
 */
@Data
public class CodeModel implements RequestCheckerInterface {
    @ApiModelProperty(value = "验证码发送地址，手机、邮箱")
    String data;
    @ApiModelProperty(value = "校验码")
    String checkId;

    @Override
    @JSONField(serialize = false)
    public boolean isCorrect() {
        return RegexTool.isEmailOrMobile(data);
    }
}
