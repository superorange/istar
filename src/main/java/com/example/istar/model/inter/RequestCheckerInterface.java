package com.example.istar.model.inter;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.utils.Exp;
import com.example.istar.utils.ErrorMsg;
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
     * @throws Exp 抛出参数异常
     */
    default void check() throws Exp {
        if (!isCorrect()) {
            throw Exp.from(HttpStatus.BAD_REQUEST,4000, ErrorMsg.PARAM_ERROR);
        }
    }
}
