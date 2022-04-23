package com.example.istar.service.impl;

import com.example.istar.service.MyService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author tian
 */
@Service
public class MyServiceImpl implements MyService {
    @Override
    public boolean hasPurchase(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        if ("tian".equals(userName)) {
            return true;
        }
        return false;
    }
}
