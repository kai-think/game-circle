package com.kai.common.throwable;

public class  TokenUnauthorizedException extends CustomException {
    public TokenUnauthorizedException() {
        this("未授权");
    }

    public TokenUnauthorizedException(String message) {
        super(message);
        setCode(401);
    }
}
