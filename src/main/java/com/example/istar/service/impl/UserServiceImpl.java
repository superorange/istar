package com.example.istar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.entity.User;
import com.example.istar.mapper.UserMapper;
import com.example.istar.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.istar.utils.PageWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    public PageWrapper<User> queryByPageHelper(int pageIndex, int pageSize) {
        com.github.pagehelper.Page<Object> objects = PageHelper.startPage(pageIndex, pageSize, true);
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectList(null));
        objects.close();
        return PageWrapper.wrap(pageInfo);
    }


    @Override
    public User validateUser(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username).eq(User::getPassword,password);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User queryUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}
