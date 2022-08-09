package com.example.istar.utils.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(ordinal = 1)
    private Integer code;
    @JSONField(ordinal = 2)
    private String msg;
    @JSONField(ordinal = 3)
    private Object data;


    public static ErrorEntity from(ErrorException errorException) {
        return new ErrorEntity(errorException.getErrorCode(), errorException.getErrorMsg(), errorException.getException());
    }

    public static ErrorEntity from(Exception errorException) {
        return new ErrorEntity(998, errorException.getMessage(), errorException);
    }
}
