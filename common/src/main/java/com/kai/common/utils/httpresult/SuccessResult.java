package com.kai.common.utils.httpresult;


public class SuccessResult<T> extends HttpResult<T> {

    public SuccessResult(T data) {
        this.success = true;
        this.message = "操作成功";
        this.data = data;
        this.status = ResultType.SUCCESS.getCode();
    }
}
