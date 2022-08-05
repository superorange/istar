package com.example.istar.expression;

import com.example.istar.handler.LoginUser;
import com.example.istar.utils.Exp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * /TODO 自定义权限控制
 *
 * @author tian
 */
@Service("userExpression")
@Slf4j
public class UserExpression {
    public boolean hasPermission() {
        LoginUser loginUser = LoginUser.getCurrentUser();
        return false;
    }

    public boolean isSuperAdmin() throws Exp {
        return true;
//        return LoginUser.getCurrentUserAndThrow().getRoles().contains(Roles.SYS_SUPER_ADMIN);
    }


    public boolean mustLogin() throws Exp {
        return true;
//        return StrUtil.isNotEmpty(LoginUser.getUuidAndThrow());
    }

}

