package com.example.istar.model.inter;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.response.ErrorMsg;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;

/**
 * @author tian
 */
public interface RequestCheckerInterface {
    /**
     * /判断是否正确
     *
     * @return boolean 是否正确，必须重写
     */
    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false)
    boolean isCorrect();

    /**
     * 检查是否正确
     *
     * @throws ErrorException 抛出参数异常
     */
    default void check() throws ErrorException {
        if (!isCorrect()) {
            throw ErrorException.wrap(HttpStatus.BAD_REQUEST, ErrorMsg.BAD_REQUEST);
        }
    }
}
