package com.example.istar.service;

import com.example.istar.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author tian
 * @since 2022-04-18
 */
public interface IUserService extends IService<User> {

    User validateUser(String username, String password);

    User queryUserByUsername(String username);
}
