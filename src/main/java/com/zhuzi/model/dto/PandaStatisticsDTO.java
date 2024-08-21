package com.zhuzi.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 熊猫统计查询DTO参数类
 */
@Data
public class PandaStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
    * 统计维度，0：性别，1：等级
    * */
    private Integer statisticsType;
}
