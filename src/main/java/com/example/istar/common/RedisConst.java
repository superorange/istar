package com.example.istar.common;

public interface RedisConst {

    //获取用户头像通过uuid
    String user_avatar_by_uuid = "user:avatar:uuid:";
    //获取用户信息通过uuid
    String user_info_by_uuid = "user:info:uuid:";
    //获取checkId通过data，mobile、email
    String auth_cid_by_key = "auth:cid:key:";
    //获取验证码通过data，mobile、email
    String auth_code_by_key = "auth:code:key:";

}
