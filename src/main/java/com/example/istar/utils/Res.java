package com.example.istar.utils;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tian
 * 返回状态统一管理
 */
@Data
public class Res<T> implements Serializable {
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

    public Res(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Res(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> Res<T> ok(T data) {
        return new Res<>(200, null, data);
    }

    public static <T> Res<T> ok(String msg, T data) {
        return new Res<>(200, msg, data);
    }

    public static <T> Res<T> ok() {
        return new Res<>(200, null, null);
    }

    public static <T> Res<T> fail(String msg) {
        return new Res<>(201, msg, null);
    }

    public static <T> Res<T> fail(int code, String msg) {
        return new Res<>(code, msg, null);
    }


}
