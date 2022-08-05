package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.PageWrapperDto;
import com.example.istar.model.QueryPageModel;
import com.example.istar.model.TopicCommentReplayModel;
import com.example.istar.entity.CommentEntity;
import com.example.istar.entity.ReplyEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.ReplyServiceImpl;
import com.example.istar.service.impl.CommentServiceImpl;
import com.example.istar.utils.CommonUtil;
import com.example.istar.utils.Exp;
import com.example.istar.utils.Res;
import com.example.istar.utils.ErrorMsg;
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
@Api(tags = "评论回复接口")
@RequestMapping("/replay")
public class ReplyController {

    @Resource
    private CommentServiceImpl commentService;
    @Resource
    private ReplyServiceImpl replyService;

    @ApiOperation(value = "新增回复")
    @PostMapping("")
    public Res<ReplyEntity> addReply(TopicCommentReplayModel model) throws Exp {
        model.check();
        //先检查评论是否还存在
        LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentEntity::getCommentId, model.getCommentId());
        CommentEntity commentEntity = commentService.getOne(wrapper);
        if (commentEntity == null || commentEntity.getStatus() != 0) {
            return Res.fail(5504, "评论已被删除，无法回复");
        }
        //如果回复不为空，检查回复是否存在
        ReplyEntity toReplyEntity = null;
        //新增一个回复对象
        ReplyEntity replyEntity = new ReplyEntity();
        replyEntity.setToReplyUuid(commentEntity.getUuid());
        if (ObjectUtil.isNotEmpty(model.getToReplyId())) {
            LambdaQueryWrapper<ReplyEntity> toReplyWrapper = new LambdaQueryWrapper<>();
            toReplyWrapper.eq(ReplyEntity::getReplyId, model.getToReplyId());
            toReplyEntity = replyService.getOne(toReplyWrapper);
            if (toReplyEntity == null || toReplyEntity.getStatus() != 0) {
                return Res.fail(5505, "回复已被删除，无法回复");
            }
            replyEntity.setToReplyUuid(toReplyEntity.getUuid());
        }
        replyEntity.setUuid(LoginUser.getUuidAndThrow());
        replyEntity.setCommentId(model.getCommentId());
        replyEntity.setReplyId(CommonUtil.generateTimeId());
        replyEntity.setToReplyId(model.getToReplyId());
        replyEntity.setContent(model.getContent());
        replyEntity.setStatus(0);
        replyEntity.setCreateTime(System.currentTimeMillis());
        replyEntity.setLikeCount(0);
        boolean save = replyService.save(replyEntity);
        return save ? Res.ok(replyEntity) : Res.fail(5506, ErrorMsg.DATABASE_ERROR);
    }

    @ApiOperation(value = "获取评论下的回复列表")
    @GetMapping("")
    public Res<PageWrapperDto<ReplyEntity>> getReplies(QueryPageModel model) throws Exp {
        LambdaQueryWrapper<ReplyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReplyEntity::getCommentId, model.getQ());
        wrapper.eq(ReplyEntity::getStatus, 0);
        wrapper.orderBy(true, model.isAsc(), ReplyEntity::getId);
        Page<ReplyEntity> page = new Page<>(model.getCurrentIndex(), model.getCurrentCount());
        Page<ReplyEntity> topicEntityPage = replyService.page(page, wrapper);
        return Res.ok(PageWrapperDto.wrap(topicEntityPage));
    }

    @ApiOperation(value = "删除回复")
    @DeleteMapping("/{id}")
    public Res<Boolean> deleteReply(@PathVariable("id") String replyId) throws Exp {
        LambdaQueryWrapper<ReplyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReplyEntity::getReplyId, replyId);
        ReplyEntity replyEntity = replyService.getOne(wrapper);
        if (ObjectUtil.isNull(replyEntity) || replyEntity.getStatus() == 3 || replyEntity.getStatus() == -3) {
            return Res.ok();
        }
        if (Roles.isSuperAdmin()) {
            replyEntity.setStatus(-3);
            return Res.ok(replyService.updateById(replyEntity));
        } else if (LoginUser.isSelf(replyEntity.getUuid())) {
            replyEntity.setStatus(3);
            return Res.ok(replyService.updateById(replyEntity));
        }
        return Res.fail(5501, ErrorMsg.RESOURCE_LOCKED);
    }


}
