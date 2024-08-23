package com.zhuzi.enums;

/**
 * excel报表任务类型
 */
public enum ExcelTaskType {
    EXPORT(0, "导出"),
    IMPORT(1, "导入"),
    ;

    private final Integer code;
    private final String desc;

    ExcelTaskType(Integer code, String desc) {
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
