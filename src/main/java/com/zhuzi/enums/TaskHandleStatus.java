package com.zhuzi.enums;

/**
 * 任务处理状态枚举类
 */
public enum TaskHandleStatus {
    WAIT_HANDLE(0, "待处理"),
    IN_PROGRESS(1, "处理中"),
    SUCCEED(2, "处理成功"),
    FAILED(3, "处理失败"),
    WAIT_TO_RESTORE(4, "等待恢复")
    ;

    private final Integer code;
    private final String desc;

    TaskHandleStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
