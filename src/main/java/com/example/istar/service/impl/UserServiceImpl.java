package com.example.istar.service.impl;

import com.example.istar.dto.PageModel;
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
    public List<User> queryUsers(PageModel model) {
        return userMapper.queryUserList(model.getOffset(), model.getCount());
    }
}
