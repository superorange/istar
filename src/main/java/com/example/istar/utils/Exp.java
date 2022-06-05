package com.example.istar.utils;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author tian
 * 返回状态统一管理
 */
@Setter
@Getter
public class Exp extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;
    @JSONField(ordinal = 1)
    private final int code;
    @JSONField(ordinal = 2)
    private final String msg;


    public Exp(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Exp(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    public static Exp from(int code, String msg) {
        return new Exp(code, msg);
    }

    public static Exp from(String msg) {
        return new Exp(ResultCode.FAILED.getCode(), msg);
    }

    public static Exp from(ResultCode resultCode) {
        return new Exp(resultCode);
    }


}
