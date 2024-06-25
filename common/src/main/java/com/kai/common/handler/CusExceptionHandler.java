package com.kai.common.handler;

import com.example.demo.common.throwable.CustomException;
import com.example.demo.common.throwable.FlowException;
import com.example.demo.utils.httpresult.FailResult;
import com.example.demo.utils.httpresult.HttpResult;
import com.example.demo.utils.httpresult.SuccessResult;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

@ControllerAdvice
@ResponseBody
public class CusExceptionHandler {

    @ExceptionHandler(FlowException.class)
    public HttpResult<String> exceptionHandler(FlowException e) {
        return new FailResult<>(e.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public HttpResult<String> exceptionHandler(CustomException e){
        e.printStackTrace();
        return new FailResult<>(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RedisSystemException.class)
    public HttpResult<String> exceptionHandler(RedisSystemException e) {
        e.printStackTrace();
        return new FailResult<>(e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public HttpResult<String> exceptionHandler(SQLException e) {
        e.printStackTrace();
        return new FailResult<>(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public HttpResult<String> exceptionHandler(Exception e){
        e.printStackTrace();
        return new FailResult<>(e.getMessage());
    }
}
