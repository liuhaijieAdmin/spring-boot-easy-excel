package com.zhuzi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举类
 */
@Getter
@AllArgsConstructor
public enum Sex {
    MALE(0, "男"),
    FEMALE(1, "女"),
    UNKNOWN(2, "未知")
    ;

    private final Integer code;
    private final String value;

    public static Integer codeOfValue(String value) {
        for (Sex sex : Sex.values()) {
            if (value.equals(sex.value)) {
                return sex.code;
            }
        }
        return UNKNOWN.code;
    }

    public static String valueOfCode(Integer code) {
        for (Sex sex : Sex.values()) {
            if (code.equals(sex.code)) {
                return sex.value;
            }
        }
        return UNKNOWN.value;
    }

}
