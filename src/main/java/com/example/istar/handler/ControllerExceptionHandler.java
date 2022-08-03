package com.example.istar.handler;

import com.example.istar.utils.Exp;
import com.example.istar.utils.R;
import com.example.istar.utils.ResultCode;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author tian
 */
@ResponseBody
@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
public class ControllerExceptionHandler {

    @ExceptionHandler(Exp.class)
    public R handleAccessExp(Exp exception) {
        return R.fail(exception.getCode(), exception.getMsg());
    }

    @ExceptionHandler(BindException.class)
    public R<List<HashMap<String, Object>>> handleAccessExp(BindException exception) {
        ArrayList<HashMap<String, Object>> objects = new ArrayList<>();
        for (FieldError error : exception.getFieldErrors()) {
            HashMap<String, Object> mapper = new HashMap<>(2);
            mapper.put("errorFiled", error.getField());
            mapper.put("errorMsg", error.getDefaultMessage());
            objects.add(mapper);
        }
        return R.fail(ResultCode.ERROR_PARAM, objects);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public R handleAccessDeniedException(AccessDeniedException exception) {
        return R.fail(ResultCode.PERMISSION_FAILED);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public R handleUsernameNotFoundException(Exception exception) {
        return R.fail(ResultCode.LOGIN_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleRepException(Exception exception) {
        return R.fail(exception.toString(), exception.getStackTrace());
    }

}
