package com.example.istar.configuration;
import com.example.istar.utils.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author tian
 */
@ResponseBody
@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
public class ControllerExceptionHandle {
    @ExceptionHandler(Exception.class)
    public R<?> handleRepException(Exception exception) {
        return R.failWithMsg(exception.getMessage());
    }
}
