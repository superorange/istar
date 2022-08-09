package com.example.istar.utils.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author tian
 * @since 2020/4/19
 * 将时间戳序列化为DateTime对象
 */
@Configuration
public class LocalDateTimeSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        if (object != null) {
            if (object instanceof Long) {
                Long seconds = (Long) object;
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(seconds / 1000, 0, ZoneOffset.ofHours(8));
                serializer.write(localDateTime);
            }
        } else {
            serializer.out.writeNull();
        }
    }
}
