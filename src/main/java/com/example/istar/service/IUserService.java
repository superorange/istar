package com.example.istar.service;

import com.example.istar.dto.impl.PageModel;
import com.example.istar.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author tian
 * @since 2022-04-18
 */
public interface IUserService extends IService<UserEntity> {
    List<UserEntity> queryUsers(PageModel model);
}
