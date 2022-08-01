package com.example.istar.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author tian
 */
public class StringToListSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {

        if (ObjectUtil.isNotEmpty(o)) {
            String str = (String) o;
            String[] strList = str.split(";");
            jsonSerializer.write(Arrays.asList(strList));
        } else {
            jsonSerializer.write(new ArrayList<>());
        }
    }
}
