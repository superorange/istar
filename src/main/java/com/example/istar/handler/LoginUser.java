package com.example.istar.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.entity.UserEntity;
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.response.ErrorMsg;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tian
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;
    private UserEntity userEntity;

    public LoginUser(UserEntity userEntity, List<String> roles) {
        this.userEntity = userEntity;
        this.roles = roles;
    }

    /**
     * /TODO 不能序列号存储到redis，会报错
     */
    @JSONField(serialize = false)
    private Collection<SimpleGrantedAuthority> authorities;
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static LoginUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        return null;
    }

    public static LoginUser getCurrentUserAndThrow() throws ErrorException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        throw ErrorException.wrap(HttpStatus.UNAUTHORIZED, ErrorMsg.UNAUTHORIZED);
    }

    public static boolean isSelf(String uuid) throws ErrorException {
        return uuid != null && LoginUser.getUuidAndThrow().equals(uuid);
    }

    public static String getUuidAndThrow() throws ErrorException {
        String uuid = getCurrentUserAndThrow().getUserEntity().getUuid();
        if (ObjectUtil.isNull(uuid)) {
            throw ErrorException.wrap(HttpStatus.UNAUTHORIZED, ErrorMsg.UNAUTHORIZED);
        }
        return uuid;
    }

    public static String getUuid() {
        LoginUser user = getCurrentUser();
        if (user != null) {
            return user.getUserEntity().getUuid();
        }
        return null;
    }
}
