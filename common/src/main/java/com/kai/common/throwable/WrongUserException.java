package com.kai.common.throwable;

public class WrongUserException extends CustomException {
    public WrongUserException() {
        this("错误的用户");
    }

    public WrongUserException(String message) {
        super(message);
        setCode(400);
    }
}
