package com.example.istar.handler;

import com.example.istar.utils.response.ErrorEntity;
import com.example.istar.utils.response.ErrorMsg;
import com.example.istar.utils.response.ErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author tian
 */
@ResponseBody
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorEntity> handleAccessExp(ErrorException exception) {
        HttpStatus httpStatus = exception.getHttpStatus();
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        int statusCode = httpStatus.value();
        ErrorEntity errorEntity = ErrorEntity.from(exception);
        errorEntity.setCode(-1);
        return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_JSON).body(errorEntity);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorEntity> handleAccessExp(AccessDeniedException exception) {
        ErrorEntity errorEntity = ErrorEntity.from(exception);
        errorEntity.setMsg(ErrorMsg.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(errorEntity);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorEntity> handleAccessExp(BindException exception) {
        ErrorEntity errorEntity = ErrorEntity.from(exception);
        ArrayList<HashMap<String, Object>> body = new ArrayList<>();
        for (FieldError error : exception.getFieldErrors()) {
            HashMap<String, Object> mapper = new HashMap<>(2);
            mapper.put("filed", error.getField());
            mapper.put("msg", error.getDefaultMessage());
            body.add(mapper);
        }
        errorEntity.setData(body);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).contentType(MediaType.APPLICATION_JSON).body(errorEntity);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEntity> handleRepException(Exception exception) {
        ErrorEntity errorEntity = ErrorEntity.from(exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errorEntity);
    }

}
