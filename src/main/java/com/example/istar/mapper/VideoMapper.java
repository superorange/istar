package com.example.istar.mapper;

import com.example.istar.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author tian
 * @since 2022-04-19
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    Video getVideoByVideoId(String videoId);

    List<Video> getVideosByKeyword(@Param("keyword") String keyword, @Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);

    List<Video> getSelfVideos(@Param("uuid") String uuid);
}
