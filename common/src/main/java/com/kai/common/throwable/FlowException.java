package com.kai.common.throwable;

public class FlowException extends CustomException {
    public FlowException() {
        this("流量控制");
    }

    public FlowException(String message) {
        super(message);
        setCode(400);
    }
}
