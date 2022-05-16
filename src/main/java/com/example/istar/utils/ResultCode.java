package com.example.istar.utils;

/**
 * @author tian
 */

public enum ResultCode {
    OK(200, "ok"),
    FAILED(201, "访问失败"),
    LOGIN_FAILED(401, "用户名或密码错误"),
    AUTH_FAILED(401, "认证失败，请检查登录信息"),
    PERMISSION_FAILED(403, "无权限访问此资源"),


    UNKNOWN_ERROR(999, "未知错误");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


}
