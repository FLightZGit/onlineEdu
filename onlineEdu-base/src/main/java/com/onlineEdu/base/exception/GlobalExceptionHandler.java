package com.onlineEdu.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author Astronaut
 * @version 1.0
 * @description
 * @date 2022/10/10 9:20
 */
@Slf4j
@RestControllerAdvice//控制器增强
public class GlobalExceptionHandler {

    //处理OnlineEduExpection异常  此类异常是程序员主动抛出的，可预知异常
    @ExceptionHandler(OnlineEduException.class)//此方法捕获OnlineEduExpection异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestErrorResponse doOnlineEduExpection(OnlineEduException e) {

        log.error("捕获异常：{}", e.getErrMessage());
        e.printStackTrace();

        String errMessage = e.getErrMessage();

        return new RestErrorResponse(errMessage);
    }


    //捕获不可预知异常 Exception
    @ExceptionHandler(Exception.class)//此方法捕获Exception异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestErrorResponse doException(Exception e) {

        log.error("捕获异常：{}", e.getMessage());
        e.printStackTrace();

        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)//此方法捕获MethodArgumentNotValidException异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestErrorResponse doMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        //校验的错误信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //收集错误
        StringBuffer errors = new StringBuffer();
        fieldErrors.forEach(error -> {
            errors.append(error.getDefaultMessage()).append(",");
        });

        return new RestErrorResponse(errors.toString());
    }
}
