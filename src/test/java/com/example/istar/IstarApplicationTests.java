package com.example.istar;

import com.example.istar.controller.LikeController;
import com.example.istar.entity.UserEntity;
import com.example.istar.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public
class IstarApplicationTests {
    @Resource
    private RedisUtil redisUtil;

    @Test
    void contextLoads() {
    }

    @Resource
    private LikeController likeController;

    @Test
    void test() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI5OGNmMjc1NS1mMDQ2LTRjN2MtOGZmOS0xZTY2YTI0NDU1YjIiLCJleHAiOjE3MzkyODU3MTd9.hswcSZqls2cYWy5VV8ds2RM4FsxrMFmLVv1iwWcSAF4";
        UserEntity cacheObject = redisUtil.getCacheObject("token:" + token);
        System.out.println(cacheObject);
    }

    @Test
    void testThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(1000000);
        int i = 0;
        while (i <= 1000000) {
            i++;
            executorService.submit(() -> {
                likeController.addCommentStar("202207312246220166ee3ebd2d2c3daf1");
                countDownLatch.countDown();
            });
        }
        executorService.shutdown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
