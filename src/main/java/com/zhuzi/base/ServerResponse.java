package com.zhuzi.base;

import com.zhuzi.enums.ResponseCode;
import com.zhuzi.exception.BusinessException;
import lombok.Data;

import java.io.Serializable;


/**
 * 响应数据
 */
@Data
public class ServerResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final ResponseCode SUCCESS = ResponseCode.SUCCESS;

    /**
     * 状态码：0表示成功，其他值表示失败
     */
    private int code = SUCCESS.getCode();

    /**
     * 消息内容
     */
    private String msg = SUCCESS.getMessage();

    /*
    * 成功标识
    * */
    private boolean success;

    /**
     * 响应数据
     */
    private T data;

    /*
    * 时间戳
    * */
    private long timestamp = System.currentTimeMillis();

    public static ServerResponse<Void> failure(ResponseCode status) {
        return new ServerResponse<>(status.getCode(), status.getMessage());
    }

    public ServerResponse<T> ok(T data) {
        this.setData(data);
        return this;
    }

    public boolean isSuccess() {
        return code == 0;
    }


    public ServerResponse() {
    }

    public ServerResponse(int code) {
        this.code = code;
    }

    public ServerResponse(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public ServerResponse(int code, String message, T data) {
        this.code = code;
        this.success = this.code == SUCCESS.getCode();
        this.msg = message;
        this.data = data;
    }


    public static ServerResponse<Void> success() {
        ServerResponse<Void> result = new ServerResponse<>();
        result.setSuccess(true);
        return result;
    }

    public static <T> ServerResponse<T> success(T data) {
        ServerResponse<T> result = new ServerResponse<>(SUCCESS.getCode());
        result.setSuccess(true);
        result.setData(data);
        return result;
    }


    public static <T> ServerResponse<T> error(int code, String message) {
        ServerResponse<T> result = new ServerResponse<>(code, message);
        result.setSuccess(false);
        return result;
    }

    public static <T> ServerResponse<T> error(Exception e) {
        return error(ResponseCode.ERROR.getCode(), e.getMessage());
    }

    public static <T> ServerResponse<T> error(BusinessException e) {
        return error(e.getCode(), e.getMessage());
    }

    public static <T> ServerResponse<T> error(BusinessException e, T data) {
        ServerResponse<T> result = error(e.getCode(), e.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> ServerResponse<T> error(String message) {
        return error(ResponseCode.ERROR.getCode(), message);
    }

    public static <T> ServerResponse<T> error(ResponseCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

}
