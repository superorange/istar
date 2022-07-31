package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.TopicCommentReplayModel;
import com.example.istar.entity.TopicCommentEntity;
import com.example.istar.entity.TopicCommentReplyEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.TopicCommentReplyServiceImpl;
import com.example.istar.service.impl.TopicCommentServiceImpl;
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
 * 回复表 前端控制器
 * code 5500-5600
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@RestController
@Api(tags = "主题评论回复接口")
@RequestMapping("/replay/topic")
public class TopicCommentReplyController {

    @Resource
    private TopicCommentServiceImpl commentService;
    @Resource
    private TopicCommentReplyServiceImpl replyService;

    @ApiOperation(value = "新增回复")
    @PostMapping("/")
    public R<TopicCommentReplyEntity> addReply(TopicCommentReplayModel model) throws Exp {
        model.check();
        TopicCommentReplyEntity replyEntity = new TopicCommentReplyEntity();
        replyEntity.setUuid(LoginUser.getUuid());

        LambdaQueryWrapper<TopicCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicCommentEntity::getCommentId, model.getCommentId());
        TopicCommentEntity commentEntity = commentService.getOne(wrapper);
        if (commentEntity == null || commentEntity.getStatus() != 0) {
            throw Exp.from(5504, "评论已被删除，无法回复");
        }
        replyEntity.setCommentId(model.getCommentId());
        replyEntity.setReplyId(CommonUtil.generateTimeId());
        replyEntity.setToReplyId(model.getToReplyId());
        replyEntity.setToReplyUuid(commentEntity.getUuid());
        replyEntity.setContent(model.getContent());
        replyEntity.setStatus(0);
        replyEntity.setCreateTime(System.currentTimeMillis());
        replyEntity.setLikeCount(0);
        boolean save = replyService.save(replyEntity);
        return save ? R.ok(replyEntity) : R.fail(5503, ResultCode.OPERATION_FAILED.getMsg());
    }

    @ApiOperation(value = "删除回复")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteReply(@PathVariable("id") String replyId) throws Exp {
        LambdaQueryWrapper<TopicCommentReplyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicCommentReplyEntity::getReplyId, replyId);
        TopicCommentReplyEntity replyEntity = replyService.getOne(wrapper);
        if (ObjectUtil.isNull(replyEntity)) {
            throw Exp.from(5502, "回复已被删除");
        }
        if (Roles.isSuperAdmin()) {
            replyEntity.setStatus(-3);
            return R.ok(replyService.updateById(replyEntity));
        } else if (LoginUser.isSelf(replyEntity.getUuid())) {
            replyEntity.setStatus(3);
            return R.ok(replyService.updateById(replyEntity));
        }
        throw Exp.from(5501, ResultCode.RESOURCE_FORBIDDEN.getMsg());
    }


}
