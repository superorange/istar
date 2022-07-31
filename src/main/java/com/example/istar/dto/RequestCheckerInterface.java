package com.example.istar.dto;

import com.example.istar.utils.Exp;
import com.example.istar.utils.ResultCode;

public interface RequestCheckerInterface {
    boolean isCorrect();

    default void check() throws Exp {
        if (!isCorrect()) {
            throw new Exp(ResultCode.ERROR_PARAM);
        }
    }
}
