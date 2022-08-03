package com.example.istar.filter;

import cn.hutool.core.util.ObjectUtil;
import com.example.istar.common.PermitUrl;
import com.example.istar.common.RedisConst;
import com.example.istar.handler.LoginUser;
import com.example.istar.utils.SafeUtil;
import com.example.istar.utils.RedisUtil;
import com.example.istar.utils.ResponseUtils;
import com.example.istar.utils.ResultCode;
import org.springframework.lang.NonNull;
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
import java.util.Arrays;

/**
 * @author tian
 * @date 2020/6/22
 * @description 校验Token合法性
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter--RUNNING");
        ///如果是PERMIT_URL接口就不要认证了
        if (Arrays.stream(PermitUrl.PERMIT_URL).anyMatch(s -> s.equals(request.getRequestURI()))) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        if (ObjectUtil.isNotEmpty(token)) {
            try {
                String uuid = SafeUtil.getUuid(token);
                if (!ObjectUtils.isEmpty(uuid)) {
                    LoginUser loginUser = redisUtil.getCacheObject(RedisConst.USER_INFO_BY_UUID + uuid);
                    if (loginUser != null) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                System.out.println("JWT异常" + e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
