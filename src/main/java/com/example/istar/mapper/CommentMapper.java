package com.example.istar.mapper;

import com.example.istar.entity.CommentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 评论表 Mapper 接口
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {

}
