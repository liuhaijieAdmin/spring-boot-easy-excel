package com.zhuzi.model.bo;

import lombok.Data;

/**
 * 熊猫统计业务对象
 */
@Data
public class PandaStatisticsBO {
    // 等级
    private String level;

    // 性别
    private Integer sex;

    // 熊的数量
    private Integer counting;

    // 身高不小于170cm的数量
    private Integer heightGte170cm;
}
