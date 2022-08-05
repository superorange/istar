package com.example.istar.handler;

import com.example.istar.utils.Exp;
import com.example.istar.utils.ResponseUtils;
import com.example.istar.utils.ErrorMsg;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * /TODO 认证失败
 *
 * @author tian
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseUtils.writeErrorInfo(response, Exp.from(HttpStatus.UNAUTHORIZED, 4000, ErrorMsg.UNAUTHORIZED));
    }
}
