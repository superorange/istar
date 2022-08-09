package com.example.istar.controller;

import com.example.istar.service.impl.CommentServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.LikeUtil;
import com.example.istar.utils.response.ResEntity;
import com.example.istar.utils.RedisUtil;
import io.swagger.annotations.Api;
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
@Api(tags = "点赞接口")
@RequestMapping("/like")
public class LikeController {
    @Resource
    private TopicServiceImpl topicService;
    @Resource
    private CommentServiceImpl commentService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private LikeUtil likeUtil;


    @ApiOperation("添加主题的喜欢")
    @PostMapping("/topic/{id}")
    public ResEntity<Void> addTopicLike(@PathVariable String id) throws ErrorException {
        likeUtil.autoIncrementTopicLike(id);
        return ResEntity.ok();
    }

    @ApiOperation("添加评论的喜欢")
    @PostMapping("/comment/{id}")
    public ResEntity<Void> addCommentLike(@PathVariable String id) throws ErrorException {
        likeUtil.autoIncrementCommentLike(id);
        return ResEntity.ok();
    }

    @ApiOperation("添加回复的喜欢")
    @PostMapping("/replay/{id}")
    public ResEntity<Void> addReplayLike(@PathVariable String id) throws ErrorException {
        likeUtil.autoIncrementReplyLike(id);
        return ResEntity.ok();
    }


}
