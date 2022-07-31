package com.example.istar.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;

import java.util.Date;

public class CommonUtil {
    public static String getCurrentTime() {
        return DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
    }

    public static String generateTimeId(String key) {
        String rid;
        if (key == null) {
            rid = UUID.randomUUID().toString();
        } else {
            rid = key;
        }
        return getCurrentTime() + MD5.create().digestHex16(rid);
    }

    public static String generateTimeId() {
        return getCurrentTime() + MD5.create().digestHex16(UUID.randomUUID().toString());
    }

}
