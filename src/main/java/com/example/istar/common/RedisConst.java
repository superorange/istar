package com.example.istar.common;

/**
 * @author tian
 */
public interface RedisConst {

    /**
     * 获取用户头像通过uuid
     */
    String USER_AVATAR_BY_UUID = "user:avatar:uuid:";
    /**
     * 获取用户信息通过uuid
     */
    String USER_INFO_BY_UUID = "user:info:uuid:";
    /**
     * 获取checkId通过data，mobile、email
     */
    String AUTH_CID_BY_KEY = "auth:cid:key:";
    /**
     * 获取验证码通过data，mobile、email
     */
    String AUTH_CODE_BY_KEY = "auth:code:key:";

    String TOPIC_LIKE_BY_TOPIC_ID = "topic:like:topicid";
    String COMMENT_LIKE_BY_COMMENT_ID = "comment:like:commentid";
}
