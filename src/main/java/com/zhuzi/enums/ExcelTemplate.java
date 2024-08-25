package com.zhuzi.enums;

/**
 * Excel模板枚举类
 */
public enum ExcelTemplate {

    PANDA_STATISTICS(1, "excel/panda_statistics_template.xlsx", "熊猫统计模板");

    private final Integer code;
    private final String path;
    private final String desc;

    ExcelTemplate(Integer code, String path, String desc) {
        this.code = code;
        this.path = path;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }

    public String getDesc() {
        return desc;
    }
}
