package com.example.istar.utils;

import org.springframework.util.ConcurrentReferenceHashMap;

public class SyncCacheUtil {
    static ConcurrentReferenceHashMap<String, Object> map = new ConcurrentReferenceHashMap<>();

    public static Object getSyncKey(String key) {
        return map.computeIfAbsent(key, k -> new Object());
    }


}
