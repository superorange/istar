package com.example.istar.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.TopicCommentModel;
import com.example.istar.entity.TopicCommentEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.TopicCommentServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.utils.CommonUtil;
import com.example.istar.utils.Exp;
import com.example.istar.utils.R;
import com.example.istar.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 评论表 前端控制器
 * code 5400-5500
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@RestController
@Api(tags = "主题评论接口")
@RequestMapping("/comment/topic")
public class TopicCommentController {
    @Resource
    private TopicCommentServiceImpl topicCommentService;
    @Resource
    private TopicServiceImpl topicService;

    /**
     * 新增评论
     */
    @ApiOperation(value = "新增评论")
    @PostMapping("")
    public R<TopicCommentEntity> addComment(TopicCommentModel model) throws Exp {
        model.check();
        LambdaQueryWrapper<TopicEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicEntity::getTopicId, model.getTopicId());
        TopicEntity topicEntity = topicService.getOne(wrapper);
        //// 判断主题是否存在
        if (topicEntity == null || topicEntity.getStatus() != 0) {
            throw Exp.from(ResultCode.NOT_FOUND);
        }
        ////新增一个评论对象
        TopicCommentEntity commentEntity = new TopicCommentEntity();
        commentEntity.setUuid(LoginUser.getUuid());
        commentEntity.setTopicId(topicEntity.getTopicId());
        //如果主题里面评论ID为空，则评论ID为主题ID
        if (StrUtil.isEmpty(topicEntity.getCommentId())) {
            commentEntity.setCommentId(CommonUtil.generateTimeId());
        } else {
            commentEntity.setCommentId(topicEntity.getCommentId());
        }
        commentEntity.setContent(model.getContent());
        commentEntity.setStatus(0);
        commentEntity.setCreateTime(System.currentTimeMillis());
        boolean b = false;
        boolean a = topicCommentService.save(commentEntity);
        if (a) {
            topicEntity.setCommentId(commentEntity.getCommentId());
            b = topicService.updateById(topicEntity);
        }
        return a == b && a ? R.ok(commentEntity) : R.fail(ResultCode.OPERATION_FAILED);
    }


    /**
     * 删除评论
     * 目前非管理员只有自己可以删除自己的
     */
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteComment(@PathVariable("id") String commentId) throws Exp {
        LambdaQueryWrapper<TopicCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicCommentEntity::getCommentId, commentId);
        TopicCommentEntity entity = topicCommentService.getOne(wrapper);
        if (entity == null) {
            throw Exp.from(ResultCode.NOT_FOUND);
        }
        if (Roles.isSuperAdmin()) {
            entity.setStatus(-3);
            return R.ok(topicCommentService.updateById(entity));
        } else if (LoginUser.isSelf(entity.getUuid())) {
            entity.setStatus(3);
            return R.ok(topicCommentService.updateById(entity));
        }
        throw Exp.from(ResultCode.PERMISSION_FAILED);

    }
}
