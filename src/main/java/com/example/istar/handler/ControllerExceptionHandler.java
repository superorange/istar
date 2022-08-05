package com.example.istar.handler;

import com.alibaba.fastjson.JSON;
import com.example.istar.utils.Exp;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class ControllerExceptionHandler {

    @ExceptionHandler(Exp.class)
    public ResponseEntity<HashMap<String, Object>> handleAccessExp(Exp exception) {
        return ResponseEntity.status(exception.getHttpStatus()).contentType(MediaType.APPLICATION_JSON).body(exception.getMapJson());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<HashMap<String, Object>>> handleAccessExp(BindException exception) {
        ArrayList<HashMap<String, Object>> body = new ArrayList<>();
        for (FieldError error : exception.getFieldErrors()) {
            HashMap<String, Object> mapper = new HashMap<>(2);
            mapper.put("filed", error.getField());
            mapper.put("msg", error.getDefaultMessage());
            body.add(mapper);
        }
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleRepException(Exception exception) {
        HashMap<String, Object> hashMap = new HashMap<>(2);
        hashMap.put("data", exception.getStackTrace());
        hashMap.put("msg", exception.getMessage());

        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(hashMap);
    }

}
