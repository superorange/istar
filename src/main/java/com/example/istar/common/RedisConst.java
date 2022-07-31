package com.example.istar.common;

public interface RedisConst {

    /**
     * 用户登录
     */
    String REDIS_LOGIN_CODE = "login:code:";
    String REDIS_LOGIN_INFO = "authentication:uuid:";
    String REDIS_LOGIN_PRE_CHECK = "authentication:check:";

    int LOGIN = 1;
}
