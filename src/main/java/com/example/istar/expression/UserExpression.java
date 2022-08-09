package com.example.istar.expression;

import cn.hutool.core.util.StrUtil;
import com.example.istar.common.Roles;
import com.example.istar.handler.LoginUser;
import com.example.istar.utils.response.ErrorException;
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
    public boolean isSuperAdmin() {
        try {
            return LoginUser.getCurrentUserAndThrow().getRoles().contains(Roles.SYS_SUPER_ADMIN);
        } catch (ErrorException e) {
            return false;
        }
    }


    public boolean mustLogin() {
        try {
            return StrUtil.isNotEmpty(LoginUser.getUuidAndThrow());
        } catch (ErrorException e) {
            return false;
        }


    }

}

