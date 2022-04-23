package com.example.istar.utils;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tian
 * 返回状态统一管理
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class R<T> {
    private int code;
    private String msg;
    /**
     * 配置为null不返回
     */
    @JSONField(serialize = false)
    private Integer total;
    private T data;


    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private R(ResultCode response, T data) {
        this.code = response.getCode();
        this.msg = response.getMsg();
        this.data = data;
    }

    private R(ResultCode response) {
        this.code = response.getCode();
        this.msg = response.getMsg();
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.OK, data);
    }

    public static <T> R<T> ok() {
        return new R<>(ResultCode.OK);
    }

    public static <T> R<T> fail(T data) {
        return new R<>(ResultCode.FAILED, data);
    }

    public static R<?> failWithMsg(String msg) {
        return new R<>(201, msg, null);
    }

    public static <T> R<T> failWithMsg(String msg, T data) {
        return new R<>(201, msg, data);
    }

    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAILED);
    }

    public static <T> R<T> custom(ResultCode code, T data) {
        return new R<>(code, data);
    }

}
