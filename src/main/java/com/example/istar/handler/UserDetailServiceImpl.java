package com.example.istar.handler;

import com.example.istar.entity.User;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.queryUserByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException(null);
        }
        return new LoginUser(user, Arrays.asList(user.getRoles().split(",")));
    }
}
