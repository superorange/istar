package com.example.istar.utils.response;

/**
 * @author tian
 */

public class ErrorMsg {
    public static String UNAUTHORIZED = "未认证的操作，请先登录";
    public static String BAD_REQUEST = "请求参数错误";
    public static String CODE_ERROR = "验证码错误";
    public static String DATABASE_ERROR = "数据库操作失败";

    public static String LOGIN_EXPIRE = "登录信息已经过期";
    public static String LOGIN_FAILED = "用户名或密码错误";
    public static String NOT_FOUND = "请求的资源不存在";
    public static String FORBIDDEN = "未授权的操作";
    public static String RESOURCE_LOCKED = "改资源已被锁定";
    public static String USER_NOT_EXIST = "用户不存在";


}
