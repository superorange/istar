package com.example.istar.utils;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author tian
 * 返回状态统一管理
 */
@Setter
@Getter
public class Exp extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;
    @JSONField(serialize = false, deserialize = false)
    private final int httpStatus;
    @JSONField(ordinal = 1)
    private final int code;
    @JSONField(ordinal = 2)
    private final String msg;

    public Exp(int httpStatus, int code, String msg) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.msg = msg;
    }

    public static Exp from(HttpStatus status, int code, String msg) {
        return new Exp(status.value(), code, msg);
    }

    public HashMap<String,Object> getMapJson(){
        HashMap<String,Object> map = new HashMap<>(2);
        map.put("code",code);
        map.put("msg",msg);
        return map;
    }


}
