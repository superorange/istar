package com.example.istar.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.*;
import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.entity.VideoEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.PicturesServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private PicturesServiceImpl picturesService;
    @Resource
    private VideosServiceImpl videosService;

    @Resource
    private MinioUtils minioUtil;

    @PostMapping("")
    @ApiOperation(value = "添加帖子")
    public R<TopicSimpleModel> addTopic(TopicModel model, @RequestParam("pictures") MultipartFile[] multipartFile) throws Exception {
        if (!model.isCorrect()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        LoginUser loginUser = LoginUser.getCurrentUser();
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
        boolean result = uploadPicAndToDb(model, multipartFile, loginUser.getUserEntity().getUuid());
        if (!result) return R.fail(277, "图片存储失败");
        //将图片List转String
        updateModelPic(model, topicEntity);
        //将视频List转String
        updateModelVideo(model, topicEntity);
        boolean save = topicService.save(topicEntity);
        return save ? R.ok(new TopicSimpleModel(topicEntity.getTopicId())) : R.fail();
    }

    private void updateModelVideo(TopicModel model, TopicEntity topicEntity) {
        if (model.getVideoIds() != null) {
            if (model.getVideoIds().size() > 0) {
                topicEntity.setVideoId(String.join(";", model.getVideoIds()));
            } else {
                topicEntity.setVideoId("");
            }
        }
    }

    private void updateModelPic(TopicModel model, TopicEntity topicEntity) {
        if (model.getPictureIds() != null) {
            if (model.getPictureIds().size() > 0) {
                topicEntity.setPicId(String.join(";", model.getPictureIds()));
            } else {
                topicEntity.setPicId("");
            }
        }
    }

    private boolean uploadPicAndToDb(TopicModel model, MultipartFile[] multipartFile, String uuid) throws Exception {
        if (multipartFile != null && multipartFile.length > 0) {
            List<MinioUtils.MinioUploadWrapper> uploadWrappers = minioUtil.uploadFile(multipartFile);
            List<PictureEntity> picObject = uploadWrappers.stream().map(f -> {
                PictureEntity pictureEntity = new PictureEntity();
                pictureEntity.setUuid(uuid);
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
    public R<TopicEntityWrapper> getTopic(@PathVariable("topicId") String topicId) {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排除不可见的帖子
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId).eq(TopicEntity::getStatus, 0);
        TopicEntity TopicEntity = topicService.getOne(lambdaQueryWrapper);
        if (TopicEntity != null) {
            TopicEntityWrapper topicWrapper = new TopicEntityWrapper();
            BeanUtils.copyProperties(TopicEntity, topicWrapper);
            ///查询图片信息
            expandPicAndVideo(topicWrapper);
            return R.ok(topicWrapper);
        }
        return R.ok();
    }

    @GetMapping("")
    @ApiOperation(value = "获取帖子列表")
    public R<PageWrapperModel<TopicEntityWrapper>> getPosts(PageModel model) {
        model = PageModel.avoidNull(model);
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getStatus, 0);
        if (StrUtil.isNotBlank(model.getQuery())) {
            lambdaQueryWrapper.like(TopicEntity::getTitle, model.getQuery());
        }
        lambdaQueryWrapper.orderBy(true, model.isAsc(), TopicEntity::getId);
        Page<TopicEntity> page = new Page<>(model.getCurrentIndex(), model.getCurrentCount());
        Page<TopicEntity> topicEntityPage = topicService.page(page, lambdaQueryWrapper);
        List<TopicEntityWrapper> wrappers = topicEntityPage.getRecords().stream().map(Topic -> {
            TopicEntityWrapper topicWrapper = new TopicEntityWrapper();
            BeanUtils.copyProperties(Topic, topicWrapper);
            return topicWrapper;
        }).collect(Collectors.toList());
        for (TopicEntityWrapper topicWrapper : wrappers) {
            expandPicAndVideo(topicWrapper);
        }
        PageWrapperModel<TopicEntityWrapper> pageWrapperModel = new PageWrapperModel<>();
        pageWrapperModel.setRows(wrappers);
        return R.ok(pageWrapperModel);
    }

    @GetMapping("/self")
    @ApiOperation(value = "获取自己的所有帖子")
    public R<List<TopicEntityWrapper>> getSelfTopics(PageModel model) {
        model = PageModel.avoidNull(model);
        List<TopicEntity> posts = topicService.querySelfPosts(model);
        List<TopicEntityWrapper> wrappers = posts.stream().map(Topic -> {
            TopicEntityWrapper topicWrapper = new TopicEntityWrapper();
            BeanUtils.copyProperties(Topic, topicWrapper);
            return topicWrapper;
        }).collect(Collectors.toList());
        for (TopicEntityWrapper topicWrapper : wrappers) {
            expandPicAndVideo(topicWrapper);
        }
        return R.ok(wrappers);
    }


    @DeleteMapping("/{topicId}")
    @ApiOperation(value = "删除帖子")
    public R<Boolean> deleteTopic(@PathVariable("topicId") String topicId) {
        LoginUser user = LoginUser.getCurrentUser();
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        TopicEntity TopicEntity = topicService.getOne(lambdaQueryWrapper);
        if (TopicEntity != null) {
            //只有是自己的帖子才可以删除
            if (user.getUserEntity().getUuid().equals(TopicEntity.getUuid())) {
                //是0才删除
                if (TopicEntity.getStatus() == 0) {
                    TopicEntity.setStatus(1);
                    boolean save = topicService.updateById(TopicEntity);
                    return save ? R.ok(true) : R.fail();
                }
                //-1禁止操作
                else if (TopicEntity.getStatus() == -1) {
                    return R.fail(ResultCode.OPERATION_FAILED);
                }
                //1直接返回
                return R.ok(true);
            }
            //超级管理员可以删除,设置状态为-2
            else if (user.getUserEntity().getRoles().equals(Roles.SYS_SUPER_ADMIN)) {
                TopicEntity.setStatus(-2);
                boolean save = topicService.updateById(TopicEntity);
                return save ? R.ok(true) : R.fail();
            }
            return R.fail(ResultCode.OPERATION_FORBIDDEN);
        }
        return R.ok(true);
    }


    @PutMapping("/{topicId}")
    @ApiOperation(value = "修改帖子")
    public R<TopicSimpleModel> updateTopic(@PathVariable("topicId") String topicId, TopicModel model, @RequestParam(value = "pictures", required = false) MultipartFile[] multipartFiles) throws Exception {
        LoginUser loginUser = LoginUser.getCurrentUser();
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, topicId);
        TopicEntity topicEntity = topicService.getOne(lambdaQueryWrapper);
        //查询是否有该资源
        if (topicEntity == null) {
            return R.fail(ResultCode.NOT_FOUND);
        }
        //判断所有权
        if (!loginUser.getUserEntity().getUuid().equals(topicEntity.getUuid())) {
            return R.fail(ResultCode.PERMISSION_FAILED);
        }
        //判断是否可以更改
        if (!Roles.hasSelfEdit(topicEntity.getStatus())) {
            return R.fail(ResultCode.RESOURCE_FORBIDDEN);
        }
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        topicEntity.setModifyTime(System.currentTimeMillis());
        if (multipartFiles != null && multipartFiles.length > 0) {
            if (model.getPictureIds() == null) {
                model.setPictureIds(new HashSet<>());
            }
            boolean result = uploadPicAndToDb(model, multipartFiles, loginUser.getUserEntity().getUuid());
            if (!result) return R.fail(277, "图片存储失败");
        }
        updateModelPic(model, topicEntity);
        updateModelVideo(model, topicEntity);
        boolean save = topicService.updateById(topicEntity);
        return save ? R.ok(new TopicSimpleModel(topicEntity.getTopicId())) : R.fail();


    }


    /**
     * 查询图片和视频信息
     *
     * @param topicWrapper 帖子信息
     */

    private void expandPicAndVideo(TopicEntityWrapper topicWrapper) {
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
    }

}
