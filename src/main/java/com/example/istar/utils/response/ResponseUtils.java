package com.example.istar.utils.response;

import com.alibaba.fastjson.JSON;
import com.example.istar.utils.response.ErrorException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 异常响应工具类
 *
 * @author tian
 */
public class ResponseUtils {

    public static void writeErrorInfo(HttpServletResponse response, ErrorException errorException) {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(JSON.toJSONString(errorException));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
