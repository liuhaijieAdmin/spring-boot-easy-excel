package com.zhuzi.exception;

import com.zhuzi.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {

    private int code;
    private String msg;
    private ResponseCode errorCode;
    private Object data;

    public BusinessException(ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
        this.msg = errorCode.getMessage();
    }

    public BusinessException(String msg) {
        super(msg);
        this.code = ResponseCode.EXCEPTION.getCode();
        this.msg = msg;
    }

    public BusinessException(String msg, Throwable cause, int code) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(int code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BusinessException(ResponseCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public static BusinessException of(ResponseCode response) {
        return new BusinessException(response.getCode(), response.getMessage());
    }

    public static BusinessException of(Throwable cause) {
        return new BusinessException(ResponseCode.ERROR.getMessage(), cause, ResponseCode.ERROR.getCode());
    }

    public static BusinessException of(int code, String message) {
        return new BusinessException(code, message);
    }

    public static BusinessException of(int code, String message, Throwable cause) {
        return new BusinessException(message, cause, code);
    }

}
