package com.example.istar.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.Roles;
import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.VideoEntity;
import com.example.istar.service.impl.PicturesServiceImpl;
import com.example.istar.service.impl.VideosServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tian
 */
@Component
public class VideoUtil {

    @Resource
    private VideosServiceImpl videosService;
    @Resource
    private MinioUtil minioUtil;

    public List<VideoEntity> getEntities(List<String> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return null;
        }
        LambdaQueryWrapper<VideoEntity> picturesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        picturesLambdaQueryWrapper.in(VideoEntity::getVideoId, ids);
        picturesLambdaQueryWrapper.eq(VideoEntity::getStatus, Roles.PUBLIC_SEE);
        return videosService.list(picturesLambdaQueryWrapper).stream().peek(pictures -> {
            ///添加图片地址前缀
            pictures.setVideoUrl(minioUtil.getBasisUrl() + pictures.getVideoId());
        }).collect(Collectors.toList());
    }
}
