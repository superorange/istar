package com.example.istar.model.inter;

import com.example.istar.utils.Exp;
import com.example.istar.utils.ResultCode;

/**
 * @author tian
 */
public interface RequestCheckerInterface {
    /**
     * /判断是否正确
     *
     * @return boolean 是否正确，必须重写
     */
    boolean isCorrect();

    /**
     * 检查是否正确
     *
     * @throws Exp 抛出参数异常
     */
    default void check() throws Exp {
        if (!isCorrect()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
    }
}
