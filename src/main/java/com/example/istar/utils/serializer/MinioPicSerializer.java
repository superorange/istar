package com.example.istar.utils.serializer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.example.istar.utils.MinioUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;

public class MinioPicSerializer implements ObjectSerializer {
    @Resource
    private MinioUtil minioUtil;

    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        if (o instanceof String && StrUtil.isNotEmpty((String) o)) {
            jsonSerializer.write(minioUtil.assembleUrl((String) o));
        } else {
            jsonSerializer.write(null);
        }
    }
}
