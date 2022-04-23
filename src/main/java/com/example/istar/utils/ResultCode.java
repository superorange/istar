package com.example.istar.utils;

/**
 * @author tian
 */

@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum ResultCode {
    OK(200, "ok"),
    FAILED(201, "failed"),
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
