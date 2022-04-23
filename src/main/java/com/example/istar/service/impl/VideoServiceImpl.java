package com.example.istar.service.impl;

import com.example.istar.entity.Video;
import com.example.istar.mapper.VideoMapper;
import com.example.istar.model.VideoQueryModel;
import com.example.istar.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tian
 * @since 2022-04-19
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    public Video getVideoByVideoId(String videoId) {
        return baseMapper.getVideoByVideoId(videoId);
    }

    public List<Video> getVideosByKeyword(VideoQueryModel model) {
        return baseMapper.getVideosByKeyword(model.getKeyword(), model.getLimitIndex(), model.getPageSize());
    }

    public List<Video> getSelfVideos(String uuid) {
        return baseMapper.getSelfVideos(uuid);
    }
}
