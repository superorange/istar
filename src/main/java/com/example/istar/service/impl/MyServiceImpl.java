package com.example.istar.service.impl;

import com.example.istar.service.MyService;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

/**
 * @author tian
 */
@Service
public class MyServiceImpl implements MyService {
    @Override
    public boolean hasPurchase(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        String userName1 = request.getParameter("userName");
        if ("tian".equals(userName1)) {
            return true;
        }
        return false;
    }
}
