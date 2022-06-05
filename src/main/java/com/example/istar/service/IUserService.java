package com.example.istar.service;

import com.example.istar.dto.PageModel;
import com.example.istar.dto.UserQueryModel;
import com.example.istar.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.istar.utils.PageWrapper;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author tian
 * @since 2022-04-18
 */
public interface IUserService extends IService<User> {
    User queryUser(UserQueryModel userQueryModel);

    PageWrapper<User> queryUsers(PageModel model, boolean hasTotal);
}
