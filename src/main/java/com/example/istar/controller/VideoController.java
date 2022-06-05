package com.example.istar.controller;

import com.example.istar.entity.Video;
import com.example.istar.dto.VideoQueryModel;
import com.example.istar.service.impl.VideoServiceImpl;
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
 * @since 2022-04-19
 */
@Api(tags = "视频接口")
@RestController
@RequestMapping("/video")
public class VideoController {
    @Resource
    private VideoServiceImpl videoService;

    @ApiOperation(value = "获取单个视频", notes = "根据id获取单个视频")
    @PostMapping("/getVideo/{videoId}")
    public R<Video> getVideo(@PathVariable String videoId) {
        return R.ok(videoService.getVideoByVideoId(videoId));
    }

    @ApiOperation(value = "获取多个视频", notes = "根据关键字获取多个视频")
    @GetMapping("/getVideos")
    public R<List<Video>> getVideos(VideoQueryModel model) {
        return R.ok(videoService.getVideosByKeyword(model));
    }

    @ApiOperation(value = "获取自己的视频", notes = "根据登录Header里面的Token获取自己的视频")
    @GetMapping("/getSelfVideos")
    public R<List<Video>> getSelfVideos(@RequestHeader("Authorization") String token) {
        return R.ok(videoService.getSelfVideos(token));
    }

}
