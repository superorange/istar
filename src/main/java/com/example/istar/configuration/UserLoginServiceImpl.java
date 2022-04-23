package com.example.istar.configuration;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * @author tian
 */
@Service
public class UserLoginServiceImpl implements UserDetailsService {
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"admin".equals(username)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        String encode = passwordEncoder.encode("123");
        return new User(username, encode, AuthorityUtils.commaSeparatedStringToAuthorityList(
                "admin,common"
        ));

    }
}
