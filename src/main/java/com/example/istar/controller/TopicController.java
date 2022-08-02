package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Const;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.*;
import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.entity.UserEntity;
import com.example.istar.entity.VideoEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.model.TopicModel;
import com.example.istar.model.TopicQueryModel;
import com.example.istar.service.impl.PicturesServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
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
    /**
     * 自己可见
     */
    @Resource
    private MinioUtils minioUtil;

    @PostMapping("")
    @ApiOperation(value = "添加帖子")
    public R<TopicSimpleDto> addTopic(TopicModel model) throws Exception {
        model.check();
        LoginUser loginUser = LoginUser.getCurrentUserAndThrow();
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setUuid(loginUser.getUserEntity().getUuid());
        topicEntity.setTopicId(CommonUtil.generateTimeId());
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        topicEntity.setStatus(0);
        topicEntity.setCreateTime(System.currentTimeMillis());
        topicEntity.setModifyTime(System.currentTimeMillis());
        ///新增，现在先这样做，置空
        model.setPictureIds(new HashSet<>());
        boolean result = uploadPicAndToDb(model);
        if (!result) {
            return R.fail(277, "图片存储失败");
        }
        updateModelPic(model, topicEntity);
        updateModelVideo(model, topicEntity);
        boolean save = topicService.save(topicEntity);
        return save ? R.ok(new TopicSimpleDto(topicEntity.getTopicId())) : R.fail();
    }

    /**
     * 将视频List转String
     */
    private void updateModelVideo(TopicModel model, TopicEntity topicEntity) {
        if (ObjectUtil.isNotEmpty(model.getVideoIds())) {
            topicEntity.setVideoId(String.join(";", model.getVideoIds()));
        }
    }

    /**
     * 将图片List转String
     */
    private void updateModelPic(TopicModel model, TopicEntity topicEntity) {
        if (ObjectUtil.isNotEmpty(model.getPictureIds())) {
            topicEntity.setPicId(String.join(";", model.getPictureIds()));
        }
    }

    private boolean uploadPicAndToDb(TopicModel model) throws Exception {
        if (ObjectUtil.isNotEmpty(model.getPictures())) {
            List<MinioUtils.MinioUploadWrapper> uploadWrappers = minioUtil.uploadFile(model.getPictures());
            List<PictureEntity> picObject = uploadWrappers.stream().map(f -> {
                PictureEntity pictureEntity = new PictureEntity();
                pictureEntity.setUuid(LoginUser.getUuid());
                pictureEntity.setPicId(f.getFileId());
                model.getPictureIds().add(f.getFileId());
                pictureEntity.setPicUrl(f.getFileBucketName());
                pictureEntity.setPicName(f.getFileOriginName());
                pictureEntity.setPicType("TOPIC");
                pictureEntity.setStatus(0);
                pictureEntity.setCreateTime(System.currentTimeMillis());
                return pictureEntity;
            }).collect(Collectors.toList());
            return picturesService.saveBatch(picObject);
        }
        return true;

    }


    @GetMapping("/{topicId}")
    @ApiOperation(value = "获取帖子")
    public R<TopicEntityWrapperDto> getTopic(@PathVariable("topicId") String topicId) {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排除不可见的帖子
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId).eq(TopicEntity::getStatus, 0);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        if (topicEntity != null) {
            TopicEntityWrapperDto topicWrapper = new TopicEntityWrapperDto();

            BeanUtils.copyProperties(topicEntity, topicWrapper);
            ///查询图片信息
            expandPicAndVideo(topicWrapper);
            topicWrapper.setLikeCount(likeUtil.getTopicLike(topicWrapper.getTopicId()));
            return R.ok(topicWrapper);
        }
        return R.ok();
    }

    @GetMapping("")
    @ApiOperation(value = "获取帖子列表")
    public R<PageWrapperDto<TopicEntityWrapperDto>> getTopics(TopicQueryModel model) throws Exp {
        //数据校验
        if (ObjectUtil.isNull(model)) {
            model = new TopicQueryModel();
        }
        //从数据库查询数据
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //如果是查询自己的
        if (Const.SELF.equals(model.getOwner())) {
            LoginUser currentUser = LoginUser.getCurrentUser();
            if (ObjectUtil.isNull(currentUser)) {
                throw Exp.from(ResultCode.AUTH_FAILED);
            }
            //查出自己可见没有封禁的帖子
            lambdaQueryWrapper.in(TopicEntity::getStatus, Roles.ownerSee);
        } else {
            //查出状态为0,没有封禁的帖子
            lambdaQueryWrapper.eq(TopicEntity::getStatus, 0);

        }
        //like语句模糊搜索关键字
        if (StrUtil.isNotBlank(model.getQ())) {
            lambdaQueryWrapper.like(TopicEntity::getTitle, model.getQ());
        }
        //排序
        lambdaQueryWrapper.orderBy(true, model.isAsc(), TopicEntity::getId);
        //分页查询
        Page<TopicEntity> page = new Page<>(model.getCurrentIndex(), model.getCurrentCount());
        Page<TopicEntity> topicEntityPage = topicService.page(page, lambdaQueryWrapper);
        //查出帖子后复制到新的包装对象
        List<TopicEntityWrapperDto> wrappers = topicEntityPage.getRecords().stream().map(topic -> {
            TopicEntityWrapperDto topicWrapper = new TopicEntityWrapperDto();
            BeanUtils.copyProperties(topic, topicWrapper);
            topicWrapper.setLikeCount(likeUtil.getTopicLike(topicWrapper.getTopicId()));
            return topicWrapper;
        }).collect(Collectors.toList());
        //查询帖子对应的视频和图片
        for (TopicEntityWrapperDto topicWrapper : wrappers) {
            expandPicAndVideo(topicWrapper);
        }
        PageWrapperDto<TopicEntityWrapperDto> pageWrapperModel = new PageWrapperDto<>();
        pageWrapperModel.setRows(wrappers);
        return R.ok(pageWrapperModel);
    }

    @DeleteMapping("/{topicId}")
    @ApiOperation(value = "删除帖子")
    public R<Boolean> deleteTopic(@PathVariable("topicId") String topicId) {
        LoginUser user = LoginUser.getCurrentUser();
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        if (topicEntity != null) {
            //只有是自己的帖子才可以删除
            if (user.getUserEntity().getUuid().equals(topicEntity.getUuid())) {
                //是0才删除
                if (topicEntity.getStatus() == 0) {
                    topicEntity.setStatus(1);
                    boolean save = topicService.updateById(topicEntity);
                    return save ? R.ok(true) : R.fail();
                }
                //-1禁止操作
                else if (topicEntity.getStatus() == -1) {
                    return R.fail(ResultCode.OPERATION_FAILED);
                }
                //1直接返回
                return R.ok(true);
            }
            //超级管理员可以删除,设置状态为-2
            else if (user.getUserEntity().getRoles().equals(Roles.SYS_SUPER_ADMIN)) {
                topicEntity.setStatus(-2);
                boolean save = topicService.updateById(topicEntity);
                return save ? R.ok(true) : R.fail();
            }
            return R.fail(ResultCode.OPERATION_FORBIDDEN);
        }
        return R.ok(true);
    }


    @PutMapping("/{topicId}")
    @ApiOperation(value = "修改帖子")
    public R<TopicSimpleDto> updateTopic(@PathVariable("topicId") String topicId, TopicModel model) throws Exception {
        LoginUser loginUser = LoginUser.getCurrentUserAndThrow();
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        //查询是否有该资源
        if (topicEntity == null) {
            return R.fail(ResultCode.NOT_FOUND);
        }
        //判断所有权
        if (!LoginUser.getUuidAndThrow().equals(topicEntity.getUuid())) {
            return R.fail(ResultCode.PERMISSION_FAILED);
        }
        //判断是否可以更改
        if (!Roles.hasSelfEdit(topicEntity.getStatus())) {
            return R.fail(ResultCode.RESOURCE_FORBIDDEN);
        }
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        topicEntity.setModifyTime(System.currentTimeMillis());
        if (ObjectUtil.isNotEmpty(model.getPictures())) {
            if (model.getPictureIds() == null) {
                model.setPictureIds(new HashSet<>());
            }
            boolean result = uploadPicAndToDb(model);
            if (!result) {
                return R.fail(277, "图片存储失败");
            }
        }
        updateModelPic(model, topicEntity);
        updateModelVideo(model, topicEntity);
        boolean save = topicService.updateById(topicEntity);
        return save ? R.ok(new TopicSimpleDto(topicEntity.getTopicId())) : R.fail("数据库更新失败");
    }


    /**
     * 查询图片和视频信息
     *
     * @param topicWrapper 帖子信息
     */

    private void expandPicAndVideo(TopicEntityWrapperDto topicWrapper) {
        ///查询图片信息
        if (ObjectUtil.isNotEmpty(topicWrapper.getPicId())) {
            LambdaQueryWrapper<PictureEntity> picturesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            picturesLambdaQueryWrapper.in(PictureEntity::getPicId, Arrays.asList(topicWrapper.getPicId().split(";")));
            List<PictureEntity> collect = picturesService.list(picturesLambdaQueryWrapper).stream().filter(pictures -> {
                ///添加图片地址前缀
                pictures.setPicUrl(minioUtil.getBasisUrl() + pictures.getPicUrl());
                ///去除违规图片
                return pictures.getStatus() == 0;
            }).collect(Collectors.toList());
            topicWrapper.setPictures(collect);
        }
        ///查询视频信息
        if (ObjectUtil.isNotEmpty(topicWrapper.getVideoId())) {
            LambdaQueryWrapper<VideoEntity> videosLambdaQueryWrapper = new LambdaQueryWrapper<>();
            videosLambdaQueryWrapper.in(VideoEntity::getVideoId, Arrays.asList(topicWrapper.getVideoId().split(";")));
            List<VideoEntity> collect = videosService.list(videosLambdaQueryWrapper).stream().filter(videos -> {
                ///去除违规图片
                return videos.getStatus() == 0;
            }).collect(Collectors.toList());
            topicWrapper.setVideos(collect);
        }
        LambdaQueryWrapper<UserEntity> userEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<UserEntity> queryWrapper = userEntityLambdaQueryWrapper.eq(UserEntity::getUuid, topicWrapper.getUuid());
        UserEntity user = userService.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(user)) {
            topicWrapper.setAvatarUrl(user.getAvatarUrl());
            topicWrapper.setNickName(user.getNickName());
        }

    }

}
