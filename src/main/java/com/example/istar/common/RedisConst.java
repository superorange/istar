package com.example.istar.common;

public interface RedisConst {

    /**
     * 用户登录
     */
    String REDIS_LOGIN_CODE = "login:code:";
    String REDIS_LOGIN_INFO = "authentication:uuid:";
    String REDIS_LOGIN_PRE_CHECK = "authentication:check:";

    String REDIS_TOPIC_LIKE = "topic:like:";
    String REDIS_COMMENT_LIKE = "comment:like:";
    int LOGIN = 1;
}
