package com.example.istar.controller;

import com.example.istar.service.impl.CommentServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.utils.LikeUtil;
import com.example.istar.utils.R;
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
    public R addTopicLike(@PathVariable String id) {
        boolean b = likeUtil.autoIncrementTopicLike(id);
        return b ? R.ok() : R.fail();
    }

    @ApiOperation("添加评论的喜欢")
    @PostMapping("/comment/{id}")
    public R addCommentLike(@PathVariable String id) {
        boolean b = likeUtil.autoIncrementCommentLike(id);
        return b ? R.ok() : R.fail();
    }

    @ApiOperation("添加回复的喜欢")
    @PostMapping("/replay/{id}")
    public R addReplayLike(@PathVariable String id) {
        boolean b = likeUtil.autoIncrementReplyLike(id);
        return b ? R.ok() : R.fail();
    }


}
