package com.example.istar.filter;

import cn.hutool.core.util.ObjectUtil;
import com.example.istar.handler.LoginUser;
import com.example.istar.utils.JwtUtil;
import com.example.istar.utils.RedisCache;
import com.example.istar.utils.ResponseUtils;
import com.example.istar.utils.ResultCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tian
 * @date 2020/6/22
 * @description 校验Token合法性
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ///如果是登录接口就不要认证了
        if (request.getRequestURI().equals("/user/login") || request.getRequestURI().equals("/user/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        if (ObjectUtil.isNotEmpty(token)) {
            try {
                String uuid = JwtUtil.getUuid(token);
                if (!ObjectUtils.isEmpty(uuid)) {
                    LoginUser loginUser = redisCache.getCacheObject(JwtUtil.REDIS_TOKEN_KEY + uuid);
                    if (loginUser != null) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                        return;
                    }
                    ResponseUtils.writeErrorInfo(response, ResultCode.AUTH_FAILED);
                    return;
                }
            } catch (Exception e) {
                ResponseUtils.writeErrorInfo(response, ResultCode.AUTH_FAILED);
                return;
            }

        }
        filterChain.doFilter(request, response);

    }
}
