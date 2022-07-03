package com.example.istar.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.utils.RegexTool;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * @author tian
 */
@Data
public class LoginModel {
    String key;
    String code;

    @JSONField(serialize = false)
    public boolean isBadFormat() {
        return (ObjectUtils.isEmpty(key) || ObjectUtils.isEmpty(code) || code.length() != 6)
                && RegexTool.isEmailOrMobile(key);
    }


}
