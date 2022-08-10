package com.example.istar.mapper;

import com.example.istar.entity.TopicEntity;
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
public interface TopicMapper extends BaseMapper<TopicEntity> {

    /**
     * 通过实体更新文章
     *
     * @param entity 文章实体
     * @return Integer  更新成功的数量
     */
    Integer updateByEntity(TopicEntity entity);

    /**
     * 更新状态
     *
     * @param entity 文章实体
     * @return Integer  更新成功的数量
     */
    Integer updateStatus(TopicEntity entity);

    /**
     * 更新喜欢数
     *
     * @param entity 文章实体
     * @return Integer  更新成功的数量
     */
    Integer updateLikeCount(TopicEntity entity);
}
