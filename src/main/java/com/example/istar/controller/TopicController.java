package com.example.istar.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.Roles;
import com.example.istar.dto.PageModel;
import com.example.istar.dto.TopicEntityWrapper;
import com.example.istar.dto.TopicSimpleModel;
import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.entity.VideoEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.model.PostModel;
import com.example.istar.service.impl.PicturesServiceImpl;
import com.example.istar.service.impl.TopicServiceImpl;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.Exp;
import com.example.istar.utils.MinioUtils;
import com.example.istar.utils.R;
import com.example.istar.utils.ResultCode;
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
    public R<TopicSimpleModel> addPost(PostModel model, @RequestParam("pictures") MultipartFile[] multipartFile) throws Exception {
        if (!model.isCorrect()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        LoginUser loginUser = LoginUser.getCurrentUser();
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setUuid(loginUser.getUserEntity().getUuid());
        topicEntity.setTopicId(DateUtil.format(new Date(), "yyyyMMddHHmmss") + "-" + UUID.fastUUID());
        topicEntity.setTitle(model.getTitle());
        topicEntity.setContent(model.getContent());
        topicEntity.setStatus(0);
        topicEntity.setCreateTime(System.currentTimeMillis());
        if (model.getPictureIds() == null) {
            model.setPictureIds(new ArrayList<>());
        }
        boolean result = uploadPicAndToDb(model, multipartFile, loginUser);
        if (!result) return R.fail(277, "图片存储失败");
        //插入图片
        if (model.getPictureIds() != null && model.getPictureIds().size() > 0) {
            topicEntity.setPicId(String.join(";", model.getPictureIds()));
        }
        //插入视频
        if (model.getVideoIds() != null && model.getVideoIds().size() > 0) {
            topicEntity.setVideoId(String.join(";", model.getVideoIds()));
        }
        boolean save = topicService.save(topicEntity);
        return save ? R.ok(new TopicSimpleModel(topicEntity.getTopicId())) : R.fail();
    }

    private boolean uploadPicAndToDb(PostModel model, MultipartFile[] multipartFile, LoginUser loginUser) throws Exception {
        if (multipartFile.length > 0) {
            List<MinioUtils.MinioUploadWrapper> uploadWrappers = minioUtil.uploadFile(multipartFile);
            List<PictureEntity> picObject = uploadWrappers.stream().map(f -> {
                PictureEntity pictureEntity = new PictureEntity();
                pictureEntity.setUuid(loginUser.getUserEntity().getUuid());
                pictureEntity.setPicId(f.getShortId());
                model.getPictureIds().add(f.getShortId());
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


    @GetMapping("/{postId}")
    @ApiOperation(value = "获取帖子")
    public R<TopicEntityWrapper> getPost(@PathVariable("postId") String postId) {
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排除不可见的帖子
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, postId).eq(TopicEntity::getStatus, 0);
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
    @ApiOperation(value = "获取自己的所有帖子")
    public R<List<TopicEntityWrapper>> getPosts(PageModel model) {
        if (model == null) {
            model = new PageModel();
        }
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

    @DeleteMapping("/{postId}")
    @ApiOperation(value = "删除帖子")
    public R<Boolean> deletePost(@PathVariable("postId") String postId) {
        LoginUser user = LoginUser.getCurrentUser();
        LambdaQueryWrapper<TopicEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TopicEntity::getTopicId, postId);
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
