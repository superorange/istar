package com.example.istar.service.impl;

import com.example.istar.dto.PageModel;
import com.example.istar.dto.UserQueryModel;
import com.example.istar.entity.User;
import com.example.istar.mapper.UserMapper;
import com.example.istar.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.istar.utils.PageWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tian
 * @since 2022-04-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public PageWrapper<User> queryUsers(PageModel model, boolean hasTotal) {
        List<User> users = userMapper.queryUserList(model.getPageIndex(), model.getPageSize());
        Long total = (long) -1;
        if (hasTotal) {
            total = userMapper.queryTotal();
        }
        return PageWrapper.wrap(total, users);
    }

    @Override
    public User queryUser(UserQueryModel userQueryModel) {
        return userMapper.queryUser(userQueryModel);
    }

    public User queryUserByUsername(String username) {
        UserQueryModel queryModel = new UserQueryModel();
        queryModel.setUsername(username);
        return queryUser(queryModel);
    }

    public User queryUserByEmail(String email) {
        UserQueryModel queryModel = new UserQueryModel();
        queryModel.setEmail(email);
        return queryUser(queryModel);
    }

    public User queryUserById(Long id) {
        UserQueryModel queryModel = new UserQueryModel();
        queryModel.setId(id);
        return queryUser(queryModel);
    }

    public User queryUserByUuid(String uuid) {
        UserQueryModel queryModel = new UserQueryModel();
        queryModel.setUuid(uuid);
        return queryUser(queryModel);
    }

}
