package com.example.istar.mapper;

import com.example.istar.dto.PageModel;
import com.example.istar.entity.Post;
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
 * @since 2022-07-03
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
    List<Post> querySelfPosts(@Param("index") Integer index, @Param("size") Integer size, String uuid);
}
