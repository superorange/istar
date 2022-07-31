package com.example.istar.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.dto.impl.VideoQueryModel;
import com.example.istar.entity.VideoEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@Api(tags = "视频接口")
@RestController
@RequestMapping("/videos")
public class VideosController {
    @Resource
    private VideosServiceImpl videoService;

    @ApiOperation(value = "获取单个视频", notes = "根据id获取单个视频")
    @PostMapping("/getVideos/{videoId}")
    public R<VideoEntity> getVideo(@PathVariable String videoId) {
        LambdaQueryWrapper<VideoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoEntity::getVideoId, videoId);
        VideoEntity videoEntity = videoService.getOne(wrapper);
        return R.ok(videoEntity);
    }

    @ApiOperation(value = "获取多个视频", notes = "根据关键字获取多个视频")
    @GetMapping("/getVideos")
    public R<List<VideoEntity>> getVideos(VideoQueryModel model) {
        return R.ok();
    }

    @ApiOperation(value = "获取自己的视频", notes = "根据登录Header里面的Token获取自己的视频")
    @GetMapping("/getSelfVideos")
    public R<List<VideoEntity>> getSelfVideos() {
        LoginUser currentUser = LoginUser.getCurrentUser();
        LambdaQueryWrapper<VideoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoEntity::getUuid, currentUser.getUserEntity().getUuid());
        List<VideoEntity> list = videoService.list(wrapper);
        return R.ok(list);
    }
}
