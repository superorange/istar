package com.example.istar.mapper;

import com.example.istar.entity.UserEntity;
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
 * @since 2022-04-18
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
