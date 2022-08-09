package com.example.istar.utils.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author tian
 * 返回状态统一管理
 */
@Setter
@Getter
public class ErrorException extends Exception implements Serializable {
    private static long serialVersionUID = 1L;
    private int errorCode;
    private HttpStatus httpStatus;
    private Exception exception;
    private String errorMsg;

    public static ErrorException wrap(Exception exception) {
        ErrorException errorException = new ErrorException();
        errorException.setException(exception);
        return errorException;
    }

    public static ErrorException wrap(HttpStatus httpStatus, Exception exception) {
        ErrorException errorException = new ErrorException();
        errorException.setException(exception);
        errorException.setHttpStatus(httpStatus);
        return errorException;
    }
    public static ErrorException wrap(HttpStatus httpStatus) {
        ErrorException errorException = new ErrorException();
        errorException.setHttpStatus(httpStatus);
        return errorException;
    }
    public static ErrorException wrap(HttpStatus httpStatus, String errorMsg) {
        ErrorException errorException = new ErrorException();
        errorException.setHttpStatus(httpStatus);
        errorException.setErrorMsg(errorMsg);
        return errorException;
    }



}
