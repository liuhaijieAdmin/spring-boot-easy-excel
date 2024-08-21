package com.zhuzi.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PandaQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
    * 搜索关键字（兼容名称、外号、编码三个条件）
    * */
    private String keyword;

    /**
     * 生日开始时间（yyyy-MM-dd格式）
     */
    private String startTime;

    /**
     * 生日结束时间（yyyy-MM-dd格式）
     */
    private String endTime;
}
