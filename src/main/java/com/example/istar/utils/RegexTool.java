package com.example.istar.utils;

import org.springframework.util.ObjectUtils;

import java.util.regex.Pattern;

public class RegexTool {
    /**
     * 判断手机格式是否正确
     */
    private static final String check = "((\\+86|0086)?\\s*)((134[0-8]\\d{7})|(((13([0-3]|[5-9]))|(14[5-9])|15([0-3]|[5-9])|(16(2|[5-7]))|17([0-3]|[5-8])|18[0-9]|19(1|[8-9]))\\d{8})|(14(0|1|4)0\\d{7})|(1740([0-5]|[6-9]|[10-12])\\d{7}))";

    public static boolean isMobiles(String tel) {
        if (ObjectUtils.isEmpty(tel)) {
            return false;
        }
        return Pattern.matches(check, tel);
    }

    /**
     * 判断email格式是否正确
     */
    public static boolean isEmail(String email) {
        return email.matches("^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    }

    public static boolean isEmailOrMobile(String key) {
        return isEmail(key) || isMobiles(key);
    }


}
