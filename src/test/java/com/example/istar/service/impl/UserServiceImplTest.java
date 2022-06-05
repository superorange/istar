package com.example.istar.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.example.istar.IstarApplicationTests;
import com.example.istar.entity.User;
import com.example.istar.mapper.UserMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

class UserServiceImplTest extends IstarApplicationTests {
    @Resource
    private UserMapper userMapper;

    @Test
    void insert() {
        CountDownLatch latch = new CountDownLatch(20000000);
        for (int i = 0; i < 200; i++) {
            Faker faker1 = new Faker(new Locale("zh-CN"));
            Faker faker2 = new Faker();
            new Thread(() -> {
                while (latch.getCount() > 0) {
                    User user = new User();
                    user.setUuid(UUID.randomUUID().toString(false));
                    user.setUsername(faker2.name().username() + RandomUtil.randomNumbers(5));
                    user.setNickName(faker1.name().username());
                    user.setPassword(MD5.create().digestHex(UUID.fastUUID().toString(false)));
                    user.setEmail(faker2.internet().emailAddress());
                    user.setMobile(faker1.phoneNumber().cellPhone());
                    user.setGmtCreate(System.currentTimeMillis());
                    user.setGmtModified(System.currentTimeMillis());
                    user.setRoles("role_common_user");
                    try {
                        userMapper.insert(user);
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
//                    System.out.println(latch.getCount());
                    latch.countDown();
                }

            }).start();

        }
        try {
            latch.await(); // 主线程等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
