package com.example.istar.utils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author tian
 */
public class StatusSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        if (o != null) {
            if (o instanceof Integer) {
                int status = (Integer) o;
                if (status == 0) {
                    jsonSerializer.write(true);
                } else if (status == -1) {
                    jsonSerializer.write(false);
                } else if (status == 1) {
                    jsonSerializer.write(false);
                }
            }
        } else {
            jsonSerializer.write(false);
        }
    }
}
