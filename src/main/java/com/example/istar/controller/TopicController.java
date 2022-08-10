package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Roles;
import com.example.istar.configuration.MinIoClientConfig;
import com.example.istar.dto.impl.*;
import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.model.QueryPageModel;
import com.example.istar.model.TopicAddModel;
import com.example.istar.model.TopicUpdateModel;
import com.example.istar.service.impl.PicturesServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.*;
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.response.ErrorMsg;
import com.example.istar.utils.response.ResEntity;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
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
    private MinIoClientConfig minIoClientConfig;

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
    public ResEntity<TopicEntity> addTopic(TopicAddModel model) throws Exception {
        model.check();
        //生成文章实体
        TopicEntity topicEntity = generateTopic();
        topicEntity.setUuid(LoginUser.getUuidAndThrow());
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        //上传图片到minio
        List<MinioUtil.MinioUploadWrapper> minioItems = minioUtil.uploadFile(model.getPictures(), minIoClientConfig.getPictureBucketName());
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
        wrapEntity(topicEntity, pictureEntities);
        return ResEntity.ok(topicEntity);
    }


    @GetMapping("/{topicId}")
    @ApiOperation(value = "获取帖子")
    public ResEntity<TopicEntity> getTopic(@PathVariable("topicId") String topicId) throws ErrorException {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排除不可见的帖子
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        lambdaQueryWrapper.eq(TopicEntity::getStatus, Roles.PUBLIC_SEE);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        if (topicEntity == null) {
            throw ErrorException.wrap(HttpStatus.NOT_FOUND, ErrorMsg.NOT_FOUND);
        }
        wrapEntity(topicEntity, null);
        return ResEntity.ok(topicEntity);
    }

    @GetMapping("")
    @ApiOperation(value = "获取帖子列表")
    public ResEntity<PageWrapper<TopicEntity>> getTopics(QueryPageModel model) {
        //数据校验
        if (ObjectUtil.isNull(model)) {
            model = new QueryPageModel();
        }
        //从数据库查询数据
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //不为空查询单个人的
        if (StrUtil.isNotBlank(model.getKey())) {
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
        for (TopicEntity entity : topicEntityPage.getRecords()) {
            wrapEntity(entity, null);
        }
        return ResEntity.ok(PageWrapper.wrap(topicEntityPage));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除帖子")
    public ResEntity<Boolean> deleteTopic(@PathVariable String id) throws ErrorException {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, id);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        if (topicEntity != null) {
            boolean result;
            //只有是自己的帖子才可以删除
            if (LoginUser.isSelf(topicEntity.getUuid())) {
                removePic(topicEntity);
                ///删除主题
                result = topicService.remove(lambdaQueryWrapper);
            }
            //超级管理员可以删除,设置状态为-2
            else if (Roles.isSuperAdmin()) {
                removePic(topicEntity);
                ///删除主题
                result = topicService.remove(lambdaQueryWrapper);
            } else {
                throw ErrorException.wrap(HttpStatus.FORBIDDEN, ErrorMsg.FORBIDDEN);
            }
            return result ? ResEntity.ok(true) : ResEntity.fail(ErrorMsg.DATABASE_ERROR);
        }
        return ResEntity.ok(true);

    }

    private void removePic(TopicEntity topicEntity) {
        if (ObjectUtil.isNotEmpty(topicEntity.getPictureIdList())) {
            List<String> pictureIdList = topicEntity.getPictureIdList();
            List<PictureEntity> entities = pictureUtil.getEntities(pictureIdList);
            List<String> collect = entities.stream().map(PictureEntity::getPicFull).collect(Collectors.toList());
            ///数据库删除图片
            pictureUtil.remove(pictureIdList);
            ///存储删除图片
            minioUtil.removeFiles("pictures-bucket", collect);
        }

    }


    @PatchMapping("/{id}")
    @ApiOperation(value = "修改帖子")
    public ResEntity<TopicEntity> updateTopic(@PathVariable String id, TopicUpdateModel model) throws Exception {
        model.check();
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, id);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        //查询是否有该资源
        if (topicEntity == null) {
            throw ErrorException.wrap(HttpStatus.NOT_FOUND, ErrorMsg.NOT_FOUND);
        }
        //判断所有权
        if (!LoginUser.isSelf(topicEntity.getUuid())) {
            throw ErrorException.wrap(HttpStatus.FORBIDDEN, ErrorMsg.FORBIDDEN);
        }
        //判断是否可以更改
        if (!Roles.ownerCanEdit(topicEntity.getStatus())) {
            return ResEntity.fail(ErrorMsg.RESOURCE_LOCKED);
        }
        List<PictureEntity> all = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(model.getPictures())) {
            //上传图片到minio
            List<MinioUtil.MinioUploadWrapper> minioItems = minioUtil.uploadFile(model.getPictures(), minIoClientConfig.getPictureBucketName());
            //获取图片实体
            List<PictureEntity> newPicEntity = pictureUtil.getEntitiesByMinioWrapper(minioItems, LoginUser.getUuid());
            all.addAll(newPicEntity);
            boolean b = picturesService.saveBatch(newPicEntity);
            if (!b) {
                return ResEntity.fail(ErrorMsg.DATABASE_ERROR);
            }
        }
        if (ObjectUtil.isNotEmpty(model.getPictureIds())) {
            List<PictureEntity> oldPicEntity = pictureUtil.getEntities(model.getPictureIds());
            all.addAll(oldPicEntity);
        }
        topicEntity.setPictureIdList(all.stream().map(PictureEntity::getPicId).collect(Collectors.toList()));
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        topicEntity.setModifyTime(System.currentTimeMillis());
        boolean save = topicService.updateById(topicEntity);
        if (save) {
            TopicEntity result = topicService.getOne(lambdaQueryWrapper);
            wrapEntity(result, all);
            return ResEntity.ok(result);
        }
        return ResEntity.fail(ErrorMsg.DATABASE_ERROR);
    }


    private void wrapEntity(TopicEntity topicEntity, List<PictureEntity> picEntities) {
        if (ObjectUtil.isEmpty(picEntities)) {
            List<String> idList = topicEntity.getPictureIdList();
            if (ObjectUtil.isNotEmpty(idList)) {
                topicEntity.setPictures(pictureUtil.getEntities(idList)
                        .stream().peek(
                                f -> f.setPicUrl(minioUtil.assembleUrl(f.getPicFull(), minIoClientConfig.getPictureBucketName())))
                        .collect(Collectors.toList()));
            }
        } else {
            topicEntity.setPictures(picEntities
                    .stream().peek(
                            f -> f.setPicUrl(minioUtil.assembleUrl(f.getPicFull(), minIoClientConfig.getPictureBucketName())))
                    .collect(Collectors.toList()));

        }
        topicEntity.setAvatar(minioUtil.assembleUrl(userUtil.getAvatarByUuid(topicEntity.getUuid()), minIoClientConfig.getPictureBucketName()));
        topicEntity.setNickName(userUtil.getNickNameByUuid(topicEntity.getUuid()));
        topicEntity.setLikeCount(likeUtil.getTopicLike(topicEntity.getTopicId()));
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
