package com.example.istar.utils.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tian
 * 返回状态统一管理
 */
@Data
public class ResEntity<T> implements Serializable {
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

    public ResEntity(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResEntity(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResEntity<T> ok(T data) {
        return new ResEntity<>(200, null, data);
    }

    public static <T> ResEntity<T> ok(String msg, T data) {
        return new ResEntity<>(200, msg, data);
    }

    public static <T> ResEntity<T> ok() {
        return new ResEntity<>(200, null, null);
    }

    public static <T> ResEntity<T> fail(String msg) {
        return new ResEntity<>(201, msg, null);
    }

    public static <T> ResEntity<T> fail(int code, String msg) {
        return new ResEntity<>(code, msg, null);
    }


}
