package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.PageWrapperDto;
import com.example.istar.entity.TopicCommentReplyEntity;
import com.example.istar.model.QueryPageModel;
import com.example.istar.model.TopicCommentModel;
import com.example.istar.entity.TopicCommentEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.TopicCommentServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.utils.*;
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
        if (topicEntity == null) {
            return R.fail(ResultCode.NOT_FOUND);
        } else if (topicEntity.getStatus() != 0) {
            return R.fail(ResultCode.RESOURCE_FORBIDDEN);
        }
        //新增一个评论对象
        TopicCommentEntity commentEntity = new TopicCommentEntity();
        commentEntity.setUuid(LoginUser.getUuid());
        commentEntity.setTopicId(topicEntity.getTopicId());
        //设置评论ID
        commentEntity.setCommentId(CommonUtil.generateTimeId());
        commentEntity.setContent(model.getContent());
        commentEntity.setStatus(0);
        commentEntity.setCreateTime(System.currentTimeMillis());
        boolean a = topicCommentService.save(commentEntity);
        return a ? R.ok(commentEntity) : R.fail(ResultCode.OPERATION_FAILED);
    }

    @ApiOperation(value = "分页获取主题下评论")
    @GetMapping("")
    public R<PageWrapperDto<TopicCommentEntity>> getComments(QueryPageModel model) throws Exp {
        if (ObjectUtil.isNull(model)) {
            model = new QueryPageModel();
        }
        model.check();
        LambdaQueryWrapper<TopicCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicCommentEntity::getTopicId, model.getQ());
        wrapper.eq(TopicCommentEntity::getStatus, 0);
        wrapper.orderBy(true, model.isAsc(), TopicCommentEntity::getId);
        //排序
        Page<TopicCommentEntity> page = new Page<>(model.getCurrentIndex(), model.getCurrentCount());
        Page<TopicCommentEntity> entityPage = topicCommentService.page(page, wrapper);
        return R.ok(PageWrapperDto.wrapPage(entityPage));
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
            return R.fail(ResultCode.NOT_FOUND);
        }
        if (Roles.isSuperAdmin()) {
            entity.setStatus(-3);
            return R.ok(topicCommentService.updateById(entity));
        } else if (LoginUser.isSelf(entity.getUuid())) {
            entity.setStatus(3);
            return R.ok(topicCommentService.updateById(entity));
        }
        return R.fail(ResultCode.PERMISSION_FAILED);

    }
}
