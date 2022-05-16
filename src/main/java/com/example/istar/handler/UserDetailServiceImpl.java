package com.example.istar.handler;

import com.example.istar.entity.User;
import com.example.istar.service.impl.UserServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    private UserServiceImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.queryUserByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException(null);
        }
        ///TODO 查询权限信息
        List<String> strings = new ArrayList<>();
        strings.add("ROLE_USER");
        strings.add("ROLE_ROOT");
        return new LoginUser(user, strings);
    }
}
