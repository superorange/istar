package com.example.istar.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tian
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface MyService {
    boolean hasPurchase(HttpServletRequest request);
}
