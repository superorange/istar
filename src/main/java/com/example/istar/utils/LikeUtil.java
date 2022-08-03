package com.example.istar.utils;

import com.example.istar.common.RedisConst;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tian
 */
@Component
public class LikeUtil {
    @Resource
    private RedisUtil redisUtil;
    /**
     * 使用线程安全的Map集合
     */
    private static final ConcurrentHashMap<String, ReentrantLock> LOCK_MAP = new ConcurrentHashMap<>();

    public boolean autoIncrementTopicLike(String topicId) {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + topicId;
        synchronized (key.intern()) {
            Integer like = redisUtil.getCacheObject(key);
            if (like == null) {
                return false;
            } else {
                redisUtil.setCacheObject(key, ++like);
            }
            return true;

        }

//        ReentrantLock reentrantLock = LOCK_MAP.get(key);
//        if (reentrantLock == null) {
//            reentrantLock = new ReentrantLock();
//            LOCK_MAP.put(key, reentrantLock);
//        }
//        reentrantLock.lock();
//        try {
//            Integer like = redisUtil.getCacheObject(key);
//            if (like == null) {
//                return false;
//            }
//            redisUtil.setCacheObject(key, ++like);
//            return true;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            reentrantLock.unlock();
//        }


    }

    public boolean addTopicLike(String topicId, int like) {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + topicId;
        synchronized (key.intern()) {
            redisUtil.setCacheObject(key, like);
            return true;
        }
    }

    public boolean deleteTopicLike(String topicId) {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + topicId;
        return redisUtil.deleteObject(key);

    }

    public Integer getTopicLike(String topicId) {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + topicId;
        return redisUtil.getCacheObject(key);

    }

}
