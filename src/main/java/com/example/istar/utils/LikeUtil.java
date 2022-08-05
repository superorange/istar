package com.example.istar.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.RedisConst;
import com.example.istar.common.Roles;
import com.example.istar.entity.CommentEntity;
import com.example.istar.entity.ReplyEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.service.impl.CommentServiceImpl;
import com.example.istar.service.impl.ReplyServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author tian
 */
@Component
public class LikeUtil {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CommentServiceImpl commentService;
    @Resource
    private TopicServiceImpl topicService;
    @Resource
    private ReplyServiceImpl replyService;

    public void autoIncrementTopicLike(String id) throws Exp {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + id;
        synchronized (SyncCacheUtil.getSyncKey(key)) {
            Integer like = redisUtil.getCacheObject(key);
            if (like == null) {
                LambdaQueryWrapper<TopicEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(TopicEntity::getLikeCount);
                wrapper.eq(TopicEntity::getTopicId, id);
                wrapper.eq(TopicEntity::getStatus, Roles.PUBLIC_SEE);
                TopicEntity serviceOne = topicService.getOne(wrapper);
                if (serviceOne == null) {
                    throw Exp.from(HttpStatus.NOT_FOUND, 4005, ErrorMsg.NOT_FOUND);
                }
                like = serviceOne.getLikeCount();
            }
            redisUtil.setCacheObject(key, ++like);
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


    /**
     * 自增评论喜欢数量+1，插入到redis，后续定时更新到数据库
     *
     * @param id 评论ID
     */
    public void autoIncrementCommentLike(String id) throws Exp {
        String key = RedisConst.COMMENT_LIKE_BY_COMMENT_ID + id;
        synchronized (SyncCacheUtil.getSyncKey(key)) {
            Integer like = redisUtil.getCacheObject(key);
            if (like == null) {
                LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(CommentEntity::getLikeCount);
                wrapper.eq(CommentEntity::getCommentId, id);
                wrapper.eq(CommentEntity::getStatus, Roles.PUBLIC_SEE);
                CommentEntity serviceOne = commentService.getOne(wrapper);
                if (serviceOne == null) {
                    throw Exp.from(HttpStatus.NOT_FOUND, 4003, ErrorMsg.NOT_FOUND);
                }
                like = serviceOne.getLikeCount();
            }
            redisUtil.setCacheObject(key, ++like);
        }
    }

    public void autoIncrementReplyLike(String id) throws Exp {
        String key = RedisConst.REPLAY_LIKE_BY_COMMENT_ID + id;
        synchronized (SyncCacheUtil.getSyncKey(key)) {
            Integer like = redisUtil.getCacheObject(key);
            if (like == null) {
                LambdaQueryWrapper<ReplyEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(ReplyEntity::getLikeCount);
                wrapper.eq(ReplyEntity::getReplyId, id);
                wrapper.eq(ReplyEntity::getStatus, Roles.PUBLIC_SEE);
                ReplyEntity serviceOne = replyService.getOne(wrapper);
                if (serviceOne == null) {
                    throw Exp.from(HttpStatus.NOT_FOUND, 4004, ErrorMsg.NOT_FOUND);
                }
                like = serviceOne.getLikeCount();
            }
            redisUtil.setCacheObject(key, ++like);
        }
    }

    /**
     * @param id   评论ID
     * @param like 喜欢次数
     */
    public void addCommentLike(String id, int like) {
        String key = RedisConst.COMMENT_LIKE_BY_COMMENT_ID + id;
        synchronized (SyncCacheUtil.getSyncKey(key)) {
            redisUtil.setCacheObject(key, like);
        }
    }


    /**
     * @param id   主题ID
     * @param like 喜欢数量
     */
    public void addTopicLike(String id, int like) {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + id;
        synchronized (SyncCacheUtil.getSyncKey(key)) {
            redisUtil.setCacheObject(key, like);
        }
    }

    /**
     * @param id 主题ID
     * @return boolean 是否成功
     */
    public boolean deleteTopicLike(String id) {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + id;
        return redisUtil.deleteObject(key);

    }

    /**
     * @param id 主题ID
     * @return Integer 喜欢次数
     */
    public Integer getTopicLike(String id) {
        String key = RedisConst.TOPIC_LIKE_BY_TOPIC_ID + id;
        return redisUtil.getCacheObject(key);

    }

    /**
     * @param id commentId
     * @return 喜欢次数
     */
    public Integer getCommentLike(String id) {
        String key = RedisConst.COMMENT_LIKE_BY_COMMENT_ID + id;
        return redisUtil.getCacheObject(key);

    }

}
