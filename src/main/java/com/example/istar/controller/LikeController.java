package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.entity.TopicCommentEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.service.impl.TopicCommentServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.utils.LikeUtil;
import com.example.istar.utils.R;
import com.example.istar.utils.RedisUtil;
import com.example.istar.utils.ResultCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author tian
 */
@RestController
@RequestMapping("/star")
public class LikeController {
    @Resource
    private TopicServiceImpl topicService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private LikeUtil likeUtil;


    @ApiOperation("添加主题的喜欢")
    @PostMapping("/topic/{topicId}")
    public R addCommentStar(@PathVariable String topicId) {
        boolean b = likeUtil.autoIncrementTopicLike(topicId);
        if (b) {
            return R.ok();
        }
        synchronized (topicId.intern()) {
            boolean result = likeUtil.autoIncrementTopicLike(topicId);
            if (result) {
                return R.ok();
            }
            LambdaQueryWrapper<TopicEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TopicEntity::getTopicId, topicId);
            TopicEntity serviceOne = topicService.getOne(wrapper);
            if (serviceOne == null) {
                likeUtil.deleteTopicLike(topicId);
                return R.fail(ResultCode.NOT_FOUND);
            }
            Integer likeCount = serviceOne.getLikeCount();
            likeCount++;
            likeUtil.addTopicLike(topicId, likeCount);
            return R.ok();
        }


    }

}
