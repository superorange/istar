package com.example.istar.common;

public interface RedisConst {

    /**
     * 用户登录
     */
    String REDIS_CODE_LOGIN = "code:login:";
    String REDIS_LOGIN_TOKEN = "authentication:uuid:";
    int LOGIN = 1;
}
