package com.example.istar.service;

import org.springframework.http.HttpRequest;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author tian
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface MyService {
    boolean hasPurchase(HttpServletRequest request);
}
