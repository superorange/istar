package com.example.istar.expression;

import com.example.istar.handler.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

///TODO 自定义权限控制
@Component("userExpression")
public class UserExpression {
    public boolean hasPermission() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return false;
    }

    public boolean isSuperAdmin() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser.getRoles().contains("role_sys_superAdmin");
    }
}

