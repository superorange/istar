package com.example.istar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.entity.User;
import com.example.istar.mapper.UserMapper;
import com.example.istar.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    public List<User> queryUserList(Integer pageIndex, Integer pageSize) {
        return userMapper.queryUserList(pageIndex, pageSize);
    }

    public Page<User> queryUserListByMapper(Integer pageIndex, Integer pageSize) {
        Page<User> page = new Page<>(pageIndex, pageSize);
        return this.page(page, null);
    }


}
