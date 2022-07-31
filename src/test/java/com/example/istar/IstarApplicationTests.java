package com.example.istar;

import com.example.istar.entity.UserEntity;
import com.example.istar.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public
class IstarApplicationTests {
    @Resource
    private RedisUtil redisUtil;

    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI5OGNmMjc1NS1mMDQ2LTRjN2MtOGZmOS0xZTY2YTI0NDU1YjIiLCJleHAiOjE3MzkyODU3MTd9.hswcSZqls2cYWy5VV8ds2RM4FsxrMFmLVv1iwWcSAF4";
        UserEntity cacheObject = redisUtil.getCacheObject("token:" + token);
        System.out.println(cacheObject);
    }

}
