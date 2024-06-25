package com.kai.common.throwable;

public class CheckFaildedException extends CustomException {
    public CheckFaildedException() {
        this("检查错误");
    }

    public CheckFaildedException(String message) {
        super(message);
        setCode(400);
    }
}
