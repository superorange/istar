package com.example.istar.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.utils.RegexTool;
import lombok.Data;

@Data
public class CodeModel {

    String key;
    int tag;

    @JSONField(serialize = false)
    public boolean isBadFormat() {
        return !RegexTool.isEmailOrMobile(key);
    }


}
