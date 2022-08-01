package com.example.istar.model.inter;

import com.example.istar.utils.Exp;
import com.example.istar.utils.ResultCode;

public interface RequestCheckerInterface {
    ///判断是否正确
    boolean isCorrect();

    default void check() throws Exp {
        if (!isCorrect()) {
            throw new Exp(ResultCode.ERROR_PARAM);
        }
    }
}
