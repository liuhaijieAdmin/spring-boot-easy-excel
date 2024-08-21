package com.zhuzi.exception.interceptor;

import com.alibaba.excel.exception.ExcelAnalysisException;
import com.zhuzi.base.ServerResponse;
import com.zhuzi.enums.ResponseCode;
import com.zhuzi.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
* 全局异常处理器
* */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public ServerResponse<?> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return ServerResponse.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ServerResponse<String> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        ServerResponse<String> result = ServerResponse.error(e);
        result.setCode(ResponseCode.EXCEPTION.getCode());
        result.setMsg(ResponseCode.EXCEPTION.getMessage());
        return result;
    }

    @ExceptionHandler(ExcelAnalysisException.class)
    public ServerResponse<?>  excelAnalysisExceptionHandler(ExcelAnalysisException e) {
        log.error(e.getMessage(), e);
        ServerResponse<String> result = ServerResponse.error(e);
        result.setMsg(e.getMessage());
        result.setCode(ResponseCode.ANALYSIS_EXCEL_ERROR.getCode());
        return result;
    }

    /**
     * 未找到对应的Controller方法
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ServerResponse<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return ServerResponse.error(ResponseCode.RESOURCE_NOT_EXIST);
    }
}