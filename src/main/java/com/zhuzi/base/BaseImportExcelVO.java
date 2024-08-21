package com.zhuzi.base;

/**
 * excel导入-回显对象基类
 */
public class BaseImportExcelVO<T> {

    /*
    * Excel导入的结果集（可以是数据本身，也可以是OSS地址）
    * */
    private T result;

    /*
    * Excel文件导入错误信息
    * */
    private String checkMsg;

    /*
    * 本次导入是否存在校验错误的标识
    * */
    private Boolean errorFlag;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getCheckMsg() {
        return checkMsg;
    }

    public void setCheckMsg(String checkMsg) {
        this.checkMsg = checkMsg;
    }

    public Boolean getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(Boolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    public BaseImportExcelVO() {
    }

    public BaseImportExcelVO(T result, String checkMsg, Boolean errorFlag) {
        this.result = result;
        this.checkMsg = checkMsg;
        this.errorFlag = errorFlag;
    }
}
