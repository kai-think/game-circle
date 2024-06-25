package com.kai.common.throwable;

public class CustomException extends RuntimeException{
    private Integer code;
    private Object data;

    public CustomException() {}

    public CustomException(String message) {
        super(message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "code=" + code +
                ", message=" + getMessage() +
                ", data=" + data +
                '}';
    }
}
