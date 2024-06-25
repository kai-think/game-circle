package com.kai.common.throwable;

public class ExecutionException extends CustomException {
    public ExecutionException() {
        this("执行异常");
    }

    public ExecutionException(String message) {
        super(message);
        setCode(400);
    }
}
