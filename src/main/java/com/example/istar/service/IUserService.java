package com.example.istar.service;

import com.example.istar.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.istar.mapper.UserMapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author tian
 * @since 2022-04-18
 */
public interface IUserService extends IService<User> {

}
