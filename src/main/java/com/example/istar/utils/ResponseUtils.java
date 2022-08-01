package com.example.istar.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 异常响应工具类
 * @author tian
 */
public class ResponseUtils {

    public static void writeErrorInfo(HttpServletResponse response, ResultCode resultCode) {
        String jsonString = JSON.toJSONString(new R<>(resultCode));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
