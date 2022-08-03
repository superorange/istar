package com.example.istar.expression;

import cn.hutool.core.util.StrUtil;
import com.example.istar.common.Roles;
import com.example.istar.handler.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * /TODO 自定义权限控制
 *
 * @author tian
 */
@Component("userExpression")
public class UserExpression {
    public boolean hasPermission() {
        LoginUser loginUser = LoginUser.getCurrentUser();
        return false;
    }

    public boolean isSuperAdmin() {
        LoginUser loginUser = LoginUser.getCurrentUser();
        return loginUser.getRoles().contains(Roles.SYS_SUPER_ADMIN);
    }


    public boolean mustLogin() {
        return StrUtil.isNotEmpty(LoginUser.getUuid());
    }

}

