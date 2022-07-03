package com.example.istar.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.dto.VideoQueryModel;
import com.example.istar.entity.Videos;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

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
    public R<Videos> getVideo(@PathVariable String videoId) {
        LambdaQueryWrapper<Videos> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Videos::getVideoId, videoId);
        Videos videos = videoService.getOne(wrapper);
        return R.ok(videos);
    }

    @ApiOperation(value = "获取多个视频", notes = "根据关键字获取多个视频")
    @GetMapping("/getVideos")
    public R<List<Videos>> getVideos(VideoQueryModel model) {
        return R.ok();
    }

    @ApiOperation(value = "获取自己的视频", notes = "根据登录Header里面的Token获取自己的视频")
    @GetMapping("/getSelfVideos")
    public R<List<Videos>> getSelfVideos() {
        LoginUser currentUser = LoginUser.getCurrentUser();
        LambdaQueryWrapper<Videos> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Videos::getUuid, currentUser.getUser().getUuid());
        List<Videos> list = videoService.list(wrapper);
        return R.ok(list);
    }
}
