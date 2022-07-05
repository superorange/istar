package com.example.istar.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.istar.entity.UserEntity;
import com.example.istar.service.impl.UserServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Arrays;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    private UserServiceImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.getOne(new QueryWrapper<UserEntity>().eq("uuid", uuid));
        if (ObjectUtils.isEmpty(userEntity)) {
            throw new UsernameNotFoundException(null);
        }
        return new LoginUser(userEntity, Arrays.asList(userEntity.getRoles().split(",")));
    }
}
