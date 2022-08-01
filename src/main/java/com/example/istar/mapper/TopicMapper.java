package com.example.istar.mapper;

import com.example.istar.entity.TopicEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@Mapper
public interface TopicMapper extends BaseMapper<TopicEntity> {

}
