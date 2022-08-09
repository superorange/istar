package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.PageWrapper;
import com.example.istar.model.QueryPageModel;
import com.example.istar.model.TopicCommentModel;
import com.example.istar.entity.CommentEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.CommentServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.utils.*;
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.response.ErrorMsg;
import com.example.istar.utils.response.ResEntity;
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
@Api(tags = "评论接口")
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentServiceImpl topicCommentService;
    @Resource
    private TopicServiceImpl topicService;

    /**
     * 新增评论
     */
    @ApiOperation(value = "新增评论")
    @PostMapping("")
    public ResEntity<CommentEntity> addComment(TopicCommentModel model) throws Exception {
        model.check();
        LambdaQueryWrapper<TopicEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicEntity::getTopicId, model.getTopicId());
        TopicEntity topicEntity = topicService.getOne(wrapper);
        //// 判断主题是否存在
        if (topicEntity == null) {
            return ResEntity.fail(ErrorMsg.NOT_FOUND);
        }
        ///判断主题是否允许评论
        else if (!Roles.publicCanSee(topicEntity.getStatus())) {
            return ResEntity.fail(ErrorMsg.RESOURCE_LOCKED);
        }
        //新增一个评论对象
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUuid(LoginUser.getUuidAndThrow());
        commentEntity.setTopicId(topicEntity.getTopicId());
        //设置评论ID
        commentEntity.setCommentId(CommonUtil.generateTimeId());
        commentEntity.setContent(model.getContent());
        commentEntity.setStatus(Roles.PUBLIC_SEE);
        commentEntity.setCreateTime(System.currentTimeMillis());
        boolean a = topicCommentService.save(commentEntity);
        return a ? ResEntity.ok(commentEntity) : ResEntity.fail(ErrorMsg.DATABASE_ERROR);
    }

    @ApiOperation(value = "分页获取主题下评论")
    @GetMapping("")
    public ResEntity<PageWrapper<CommentEntity>> getComments(QueryPageModel model) {
        if (ObjectUtil.isNull(model)) {
            model = new QueryPageModel();
        }
        LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentEntity::getTopicId, model.getQ());
        wrapper.eq(CommentEntity::getStatus, Roles.PUBLIC_SEE);
        wrapper.orderBy(true, model.isAsc(), CommentEntity::getId);
        //排序
        Page<CommentEntity> page = new Page<>(model.getCurrentIndex(), model.getCurrentCount());
        Page<CommentEntity> entityPage = topicCommentService.page(page, wrapper);
        return ResEntity.ok(PageWrapper.wrap(entityPage));
    }

    /**
     * 删除评论
     * 目前非管理员只有自己可以删除自己的
     */
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/{id}")
    public ResEntity<Boolean> deleteComment(@PathVariable("id") String commentId) throws ErrorException {
        LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentEntity::getCommentId, commentId);
        CommentEntity entity = topicCommentService.getOne(wrapper);
        if (entity == null) {
            return ResEntity.fail(ErrorMsg.NOT_FOUND);
        }
        if (Roles.isSuperAdmin()) {
            entity.setStatus(Roles.ADMIN_DELETE);
            return ResEntity.ok(topicCommentService.updateById(entity));
        } else if (LoginUser.isSelf(entity.getUuid())) {
            entity.setStatus(Roles.SELF_DELETE);
            return ResEntity.ok(topicCommentService.updateById(entity));
        }
        return ResEntity.fail(ErrorMsg.FORBIDDEN);

    }
}
