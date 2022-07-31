package com.example.istar.filter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 网关请求响应日志打印
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/4/28 17:04
 */
//@ConditionalOnProperty(
//        prefix = "log",
//        name = {"enabled"},
//        havingValue = "true", // 关闭日志请在youlai-gateway.yaml设置 log.enabled=false
//        matchIfMissing = true  // true表示缺少log.enabled属性也会加载该bean
//)
@Component
@Slf4j
public class GatewayLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("GatewayLogFilter--RUNNING");
        TraceLog traceLog = new TraceLog();
        traceLog.setRequestIP(request.getRemoteAddr());
        String url = request.getRequestURL().toString();

        if (request.getQueryString() != null) {
            url = url + "?" + request.getQueryString();
        }
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            traceLog.setRequestHeader(authorization);
        }
        traceLog.setRequestUrl(url);
        traceLog.setRequestMethod(request.getMethod());
        long start = System.currentTimeMillis();
        traceLog.setRequestTime(DateUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_PATTERN));
        filterChain.doFilter(request, response);
        traceLog.setResponseTime(DateUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_PATTERN));
        traceLog.setExecuteTime(System.currentTimeMillis() - start);
        log.info(traceLog.toRequestString());
    }

    @Data
    public static class TraceLog {
        private String requestIP;

        /**
         * 请求路径
         */
        private String requestUrl;

        /**
         * 请求方法
         */
        private String requestMethod;

        /**
         * 请求头
         */
        private String requestHeader;
        /**
         * 请求载荷
         */
        private String requestBody;

        /**
         * 响应数据
         */
        private String responseBody;

        /**
         * 请求时间
         */
        private String requestTime;

        /**
         * 响应时间
         */
        private String responseTime;

        /**
         * 执行耗时(毫秒)
         */
        private Long executeTime;


        public String toRequestString() {
            return "\n--------请求日志-------\n" +
                    requestMethod + ' ' + requestUrl + "\n" +
                    "请求头:" + requestHeader + "\n" +
                    "请求IP:" + requestIP + '\n' +
                    "执行耗时:" + executeTime + "毫秒";
        }

        @Override
        public String toString() {
            return "\n========网关请求响应日志========\n" +
                    "请求路径:" + requestUrl + '\n' +
                    "请求方法:" + requestMethod + '\n' +
                    "请求参数:" + requestBody + '\n' +
                    "响应数据:" + responseBody + '\n' +
                    "请求时间:" + requestTime + '\n' +
                    "响应时间:" + responseTime + '\n' +
                    "执行耗时:" + executeTime + "毫秒";
        }
    }
}
