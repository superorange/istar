package com.example.istar.utils;

/**
 * @author tian
 */

public enum ResultCode {
    OK(200, "ok"),
    FAILED(201, "失败"),
    ERROR_PARAM(202, "传入的参数非法"),
    USER_EXIST(203, "用户已存在"),
    CODE_ERROR(204, "验证码错误"),
    REGISTER_ERROR(205, "注册失败"),
    OPERATION_FAILED(206, "数据库操作失败"),


    LOGIN_FAILED(401, "用户名或密码错误"),
    AUTH_FAILED(401, "认证失败，请检查登录信息"),
    NOT_FOUND(404, "没有找到"),
    PERMISSION_FAILED(403, "无权限访问此资源"),
    RESOURCE_FORBIDDEN(997, "资源已经违规，禁止操作该资源"),
    OPERATION_FORBIDDEN(998, "禁止的操作"),
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
