package com.example.istar.utils;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tian
 * 返回状态统一管理
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @JSONField(ordinal = 1)
    private final int code;
    @JSONField(ordinal = 2)
    private final String msg;
    /**
     * 配置为null不返回
     */
    @JSONField(ordinal = 3)
    private T data;

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.data = data;
    }

    public R(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.OK, data);
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R<>(ResultCode.OK.getCode(), msg, data);
    }

    public static <T> R<T> ok() {
        return new R<>(ResultCode.OK);
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(ResultCode.FAILED.getCode(), msg);
    }

    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAILED);
    }

    public static <T> R<T> fail(String msg, T data) {
        return new R<>(ResultCode.FAILED.getCode(), msg, data);
    }


    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> fail(int code, T data) {
        return new R<>(code, null, data);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode);
    }

}
