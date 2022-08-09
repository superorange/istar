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
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.response.ErrorMsg;
import com.example.istar.utils.response.ResEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
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

    @Resource
    private MinioUtil minioUtil;
    @Resource
    private UserUtil userUtil;

    @PostMapping("")
    @ApiOperation(value = "添加帖子")
    public ResEntity<TopicEntityWrapper> addTopic(TopicModel model) throws Exception {
        model.check();
        ///置空传来的pid和vid
        model.setPictureIds(new ArrayList<>());
        model.setVideoIds(new ArrayList<>());
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
            pictureEntities = pictureUtil.getEntitiesByMinioWrapper(minioItems, LoginUser.getUuid());
            //将图片实体设置到主题中
            topicEntity.setPictureIdList(pictureEntities.stream().map(PictureEntity::getPicId).collect(Collectors.toList()));
            //存储到数据库
            boolean saveBatch = picturesService.saveBatch(pictureEntities);
            if (!saveBatch) {
                return ResEntity.fail(277, "图片存储失败");
            }
        }
        boolean save = topicService.save(topicEntity);
        if (!save) {
            return ResEntity.fail(278, "文章新增失败");
        }
        //包裹实体到返回的对象
        TopicEntityWrapper dto = wrapEntity(topicEntity, pictureEntities);
        return ResEntity.ok(dto);
    }


    @GetMapping("/{topicId}")
    @ApiOperation(value = "获取帖子")
    public ResEntity<TopicEntityWrapper> getTopic(@PathVariable("topicId") String topicId) throws ErrorException {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排除不可见的帖子
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        lambdaQueryWrapper.eq(TopicEntity::getStatus, Roles.PUBLIC_SEE);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        if (topicEntity == null) {
            throw ErrorException.wrap(HttpStatus.NOT_FOUND, ErrorMsg.NOT_FOUND);
        }
        TopicEntityWrapper dto = wrapEntity(topicEntity, null);
        return ResEntity.ok(dto);
    }

    @GetMapping("")
    @ApiOperation(value = "获取帖子列表")
    public ResEntity<PageWrapper<TopicEntityWrapper>> getTopics(QueryPageModel model) {
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
            if (model.getKey().equals(LoginUser.getUuid())) {
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
        List<TopicEntityWrapper> wrappers = topicEntityPage.getRecords().stream()
                .map(topic -> wrapEntity(topic, null))
                .collect(Collectors.toList());
        return ResEntity.ok(PageWrapper.wrap(wrappers));
    }

    @DeleteMapping("/{topicId}")
    @ApiOperation(value = "删除帖子")
    public ResEntity<Boolean> deleteTopic(@PathVariable("topicId") String topicId) throws ErrorException {
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
                    return save ? ResEntity.ok(true) : ResEntity.fail(6603, ErrorMsg.DATABASE_ERROR);
                }
                return ResEntity.fail(ErrorMsg.DATABASE_ERROR);
            }
            //超级管理员可以删除,设置状态为-2
            else if (Roles.isSuperAdmin()) {
                topicEntity.setStatus(Roles.ADMIN_DELETE);
                boolean save = topicService.updateById(topicEntity);
                return save ? ResEntity.ok(true) : ResEntity.fail(6604, ErrorMsg.DATABASE_ERROR);
            }
            return ResEntity.fail(ErrorMsg.FORBIDDEN);
        }
        return ResEntity.ok(true);
    }


    @PutMapping("/{id}")
    @ApiOperation(value = "修改帖子")
    public ResEntity<TopicEntityWrapper> updateTopic(@PathVariable("id") String id, TopicModel model) throws Exception {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, id);
        lambdaQueryWrapper.select(TopicEntity::getTopicId);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        //查询是否有该资源
        if (topicEntity == null) {
            return ResEntity.fail(ErrorMsg.NOT_FOUND);
        }
        //判断所有权
        if (!LoginUser.isSelf(topicEntity.getUuid())) {
            return ResEntity.fail(ErrorMsg.FORBIDDEN);
        }
        //判断是否可以更改
        if (!Roles.ownerCanEdit(topicEntity.getStatus())) {
            return ResEntity.fail(ErrorMsg.RESOURCE_LOCKED);
        }
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        topicEntity.setModifyTime(System.currentTimeMillis());
        //上传图片到minio
        List<MinioUtil.MinioUploadWrapper> minioItems = minioUtil.uploadFile(model.getPictures());
        //获取图片实体
        List<PictureEntity> pictureEntities1 = pictureUtil.getEntitiesByMinioWrapper(minioItems, LoginUser.getUuid());
        List<PictureEntity> pictureEntities2 = pictureUtil.getEntities(model.getPictureIds());
        pictureEntities1.addAll(pictureEntities2);

        topicEntity.setPictureIdList(pictureEntities1.stream().map(PictureEntity::getPicId).collect(Collectors.toList()));

        boolean save = topicService.updateById(topicEntity);
        TopicEntityWrapper dto = wrapEntity(topicEntity, pictureEntities1);
        return save ? ResEntity.ok(dto) : ResEntity.fail(ErrorMsg.DATABASE_ERROR);
    }


    private TopicEntityWrapper wrapEntity(TopicEntity topicEntity, List<PictureEntity> picEntities) {
        TopicEntityWrapper dto = new TopicEntityWrapper();
        BeanUtils.copyProperties(topicEntity, dto);
        if (picEntities == null) {
            if (ObjectUtil.isNotEmpty(topicEntity.getPictureIdList())) {
                dto.setPictures(pictureUtil.getEntities(topicEntity.getPictureIdList()));
            }
        } else {
            dto.setPictures(picEntities);
        }
        dto.setAvatar(minioUtil.assembleUrl(userUtil.getAvatarByUuid(topicEntity.getUuid())));
        dto.setNickName(userUtil.getNickNameByUuid(topicEntity.getUuid()));
        dto.setLikeCount(likeUtil.getTopicLike(topicEntity.getTopicId()));
        return dto;
    }

    private TopicEntity generateTopic() {
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setTopicId(CommonUtil.generateTimeId());
        topicEntity.setStatus(Roles.PUBLIC_SEE);
        topicEntity.setCreateTime(System.currentTimeMillis());
        topicEntity.setModifyTime(System.currentTimeMillis());
        return topicEntity;
    }


}
