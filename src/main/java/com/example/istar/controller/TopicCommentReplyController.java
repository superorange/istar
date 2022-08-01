package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.PageWrapperDto;
import com.example.istar.model.QueryPageModel;
import com.example.istar.model.TopicCommentReplayModel;
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
@RequestMapping("/replay/comment")
public class TopicCommentReplyController {

    @Resource
    private TopicCommentServiceImpl commentService;
    @Resource
    private TopicCommentReplyServiceImpl replyService;

    @ApiOperation(value = "新增回复")
    @PostMapping("")
    public R<TopicCommentReplyEntity> addReply(TopicCommentReplayModel model) throws Exp {
        model.check();
        //先检查评论是否还存在
        LambdaQueryWrapper<TopicCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicCommentEntity::getCommentId, model.getCommentId());
        TopicCommentEntity commentEntity = commentService.getOne(wrapper);
        if (commentEntity == null || commentEntity.getStatus() != 0) {
            return R.fail(5504, "评论已被删除，无法回复");
        }
        //如果回复不为空，检查回复是否存在
        TopicCommentReplyEntity toReplyEntity = null;
        //新增一个回复对象
        TopicCommentReplyEntity replyEntity = new TopicCommentReplyEntity();
        replyEntity.setToReplyUuid(commentEntity.getUuid());
        if (ObjectUtil.isNotEmpty(model.getToReplyId())) {
            LambdaQueryWrapper<TopicCommentReplyEntity> toReplyWrapper = new LambdaQueryWrapper<>();
            toReplyWrapper.eq(TopicCommentReplyEntity::getReplyId, model.getToReplyId());
            toReplyEntity = replyService.getOne(toReplyWrapper);
            if (toReplyEntity == null || toReplyEntity.getStatus() != 0) {
                return R.fail(5505, "回复已被删除，无法回复");
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
        return save ? R.ok(replyEntity) : R.fail(5506, ResultCode.OPERATION_FAILED.getMsg());
    }

    @ApiOperation(value = "获取评论下的回复列表")
    @GetMapping("")
    public R<PageWrapperDto<TopicCommentReplyEntity>> getReplies(QueryPageModel model) throws Exp {
        model.check();
        LambdaQueryWrapper<TopicCommentReplyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicCommentReplyEntity::getCommentId, model.getQ())
                .eq(TopicCommentReplyEntity::getStatus, 0)
                .orderBy(true, model.isAsc(), TopicCommentReplyEntity::getId);
        Page<TopicCommentReplyEntity> page = new Page<>(model.getCurrentIndex(), model.getCurrentCount());
        Page<TopicCommentReplyEntity> topicEntityPage = replyService.page(page, wrapper);
        return R.ok(PageWrapperDto.wrapPage(topicEntityPage));
    }

    @ApiOperation(value = "删除回复")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteReply(@PathVariable("id") String replyId) throws Exp {
        LambdaQueryWrapper<TopicCommentReplyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicCommentReplyEntity::getReplyId, replyId);
        TopicCommentReplyEntity replyEntity = replyService.getOne(wrapper);
        if (ObjectUtil.isNull(replyEntity) || replyEntity.getStatus() == 3 || replyEntity.getStatus() == -3) {
            return R.ok();
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
