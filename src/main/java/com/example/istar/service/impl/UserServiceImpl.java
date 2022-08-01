package com.example.istar.service.impl;

import com.example.istar.model.PageModel;
import com.example.istar.entity.UserEntity;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

}
