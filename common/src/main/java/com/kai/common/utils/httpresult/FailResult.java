package com.kai.common.utils.httpresult;

public class FailResult<T> extends HttpResult<T> {

    public FailResult(String errMessage) {
        this.fail(errMessage);
    }

    public FailResult(Integer status, String errMessage) {
        this.fail(status, errMessage);
    }
}
