package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.*;
import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.model.QueryPageModel;
import com.example.istar.model.TopicModel;
import com.example.istar.service.impl.PicturesServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */

@Api(tags = "帖子接口")
@RequestMapping("/topics")
@RestController
public class TopicController {
    @Resource
    private TopicServiceImpl topicService;
    @Resource
    private UserServiceImpl userService;
    @Resource
    private PicturesServiceImpl picturesService;
    @Resource
    private VideosServiceImpl videosService;
    @Resource
    private LikeUtil likeUtil;
    @Resource
    private PictureUtil pictureUtil;
    @Resource
    private VideoUtil videoUtil;


    /**
     * 自己可见
     */
    @Resource
    private MinioUtil minioUtil;
    @Resource
    private UserUtil userUtil;

    @PostMapping("")
    @ApiOperation(value = "添加帖子")
    @PreAuthorize("@userExpression.mustLogin()")
    public R<TopicEntityWrapperDto> addTopic(TopicModel model) throws Exception {
        model.check();
        ///置空传来的pid和vid
        model.setPictureIds(new HashSet<>());
        model.setVideoIds(new HashSet<>());
        //生成文章实体
        TopicEntity topicEntity = generateTopic();
        topicEntity.setUuid(LoginUser.getUuidAndThrow());
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        //上传图片到minio
        List<MinioUtil.MinioUploadWrapper> minioItems = minioUtil.uploadFile(model.getPictures());
        List<PictureEntity> pictureEntities = null;
        if (minioItems != null) {
            //生成图片实体
            pictureEntities = pictureUtil.getEntitiesByMinioWrapper(minioItems);
            //将图片实体设置到主题中
            topicEntity.setPictureIdList(pictureEntities.stream().map(PictureEntity::getPicId).collect(Collectors.toList()));
            //存储到数据库
            boolean saveBatch = picturesService.saveBatch(pictureEntities);
            if (!saveBatch) {
                return R.fail(277, "图片存储失败");
            }
        }
        boolean save = topicService.save(topicEntity);
        if (!save) {
            return R.fail(278, "文章新增失败");
        }
        //包裹实体到返回的对象
        TopicEntityWrapperDto dto = wrapEntity(topicEntity, pictureEntities);
        return R.ok(dto);
    }


    @GetMapping("/{topicId}")
    @ApiOperation(value = "获取帖子")
    public R<TopicEntityWrapperDto> getTopic(@PathVariable("topicId") String topicId) throws Exp {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排除不可见的帖子
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        lambdaQueryWrapper.eq(TopicEntity::getStatus, 0);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        if (topicEntity == null) {
            return R.fail(ResultCode.NOT_FOUND);
        }
        TopicEntityWrapperDto dto = wrapEntity(topicEntity, null);
        return R.ok(dto);
    }

    @GetMapping("")
    @ApiOperation(value = "获取帖子列表")
    public R<PageWrapperDto<TopicEntityWrapperDto>> getTopics(QueryPageModel model) throws Exp {
        //数据校验
        if (ObjectUtil.isNull(model)) {
            model = new QueryPageModel();
        }
        //从数据库查询数据
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //不为空查询单个人的
        if (ObjectUtil.isNotEmpty(model.getKey())) {
            ///添加UUID查询条件
            lambdaQueryWrapper.eq(TopicEntity::getUuid, model.getKey());
            //判断是否是查询自己
            if (model.getKey().equals(LoginUser.getUuidAndThrow())) {
                lambdaQueryWrapper.in(TopicEntity::getStatus, Roles.OWNER_SEE);
            } else {
                lambdaQueryWrapper.eq(TopicEntity::getStatus, Roles.PUBLIC_SEE);
            }
        } else {
            lambdaQueryWrapper.eq(TopicEntity::getStatus, Roles.PUBLIC_SEE);
        }
        //like语句模糊搜索关键字
        if (StrUtil.isNotBlank(model.getQ())) {
            lambdaQueryWrapper.like(TopicEntity::getTitle, model.getQ());
            lambdaQueryWrapper.like(TopicEntity::getContent, model.getQ());
        }
        //排序
        lambdaQueryWrapper.orderBy(true, model.isAsc(), TopicEntity::getId);
        //分页查询
        Page<TopicEntity> page = new Page<>(model.getCurrentIndex(), model.getCurrentCount());
        Page<TopicEntity> topicEntityPage = topicService.page(page, lambdaQueryWrapper);
        //查出帖子后复制到新的包装对象
        List<TopicEntityWrapperDto> wrappers = topicEntityPage.getRecords().stream()
                .map(topic -> {
                    try {
                        return wrapEntity(topic, null);
                    } catch (Exp e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        return R.ok(PageWrapperDto.wrap(wrappers));
    }

    @DeleteMapping("/{topicId}")
    @ApiOperation(value = "删除帖子")
    @PreAuthorize("@userExpression.mustLogin()")
    public R<Boolean> deleteTopic(@PathVariable("topicId") String topicId) throws Exp {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(TopicEntity::getUuid, TopicEntity::getStatus);
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        if (topicEntity != null) {
            //只有是自己的帖子才可以删除
            if (LoginUser.isSelf(topicEntity.getUuid())) {
                if (Roles.ownerCanEdit(topicEntity.getStatus())) {
                    topicEntity.setStatus(Roles.SELF_DELETE);
                    boolean save = topicService.updateById(topicEntity);
                    return save ? R.ok(true) : R.fail();
                }
                return R.fail(ResultCode.OPERATION_FAILED);
            }
            //超级管理员可以删除,设置状态为-2
            else if (Roles.isSuperAdmin()) {
                topicEntity.setStatus(Roles.ADMIN_DELETE);
                boolean save = topicService.updateById(topicEntity);
                return save ? R.ok(true) : R.fail();
            }
            return R.fail(ResultCode.OPERATION_FORBIDDEN);
        }
        return R.ok(true);
    }


    @PutMapping("/{id}")
    @ApiOperation(value = "修改帖子")
    @PreAuthorize("@userExpression.mustLogin()")
    public R<TopicEntityWrapperDto> updateTopic(@PathVariable("id") String id, TopicModel model) throws Exception {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, id);
        lambdaQueryWrapper.select(TopicEntity::getTopicId);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        //查询是否有该资源
        if (topicEntity == null) {
            return R.fail(ResultCode.NOT_FOUND);
        }
        //判断所有权
        if (!LoginUser.isSelf(topicEntity.getUuid())) {
            return R.fail(ResultCode.PERMISSION_FAILED);
        }
        //判断是否可以更改
        if (!Roles.ownerCanEdit(topicEntity.getStatus())) {
            return R.fail(ResultCode.RESOURCE_FORBIDDEN);
        }
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        topicEntity.setModifyTime(System.currentTimeMillis());

        //上传图片到minio
        List<MinioUtil.MinioUploadWrapper> minioItems = minioUtil.uploadFile(model.getPictures());

        //获取图片实体
        List<PictureEntity> pictureEntities1 = pictureUtil.getEntitiesByMinioWrapper(minioItems);
        List<PictureEntity> pictureEntities2 = pictureUtil.getEntities(model.getPictureIds());
        pictureEntities1.addAll(pictureEntities2);

        topicEntity.setPictureIdList(pictureEntities1.stream().map(PictureEntity::getPicId).collect(Collectors.toList()));

        boolean save = topicService.updateById(topicEntity);
        TopicEntityWrapperDto dto = wrapEntity(topicEntity, pictureEntities1);
        return save ? R.ok(dto) : R.fail("数据库更新失败");
    }


    private TopicEntityWrapperDto wrapEntity(TopicEntity topicEntity, List<PictureEntity> pictureEntities) throws Exp {
        TopicEntityWrapperDto dto = new TopicEntityWrapperDto();
        BeanUtils.copyProperties(topicEntity, dto);
        if (pictureEntities == null) {
            if (ObjectUtil.isNotEmpty(topicEntity.getPictureIdList())) {
                dto.setPictures(pictureUtil.getEntities(new HashSet<>(topicEntity.getPictureIdList())));
            }
        } else {
            dto.setPictures(pictureEntities);
        }
        dto.setAvatar(userUtil.getAvatarByUuid(LoginUser.getUuidAndThrow()));
        dto.setNickName(userUtil.getNickNameByUuid(LoginUser.getUuidAndThrow()));
        dto.setLikeCount(likeUtil.getTopicLike(topicEntity.getTopicId()));
        return dto;
    }

    private TopicEntity generateTopic() {
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setTopicId(CommonUtil.generateTimeId());
        topicEntity.setStatus(0);
        topicEntity.setCreateTime(System.currentTimeMillis());
        topicEntity.setModifyTime(System.currentTimeMillis());
        return topicEntity;
    }


}
