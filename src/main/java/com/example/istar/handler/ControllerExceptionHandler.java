package com.example.istar.handler;

import com.example.istar.utils.Exp;
import com.example.istar.utils.R;
import com.example.istar.utils.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


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
        return R.fail(exception.getMessage(), exception);
    }
}
