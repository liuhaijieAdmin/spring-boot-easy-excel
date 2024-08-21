package com.zhuzi.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.zhuzi.converter.SexConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 熊猫统计导出VO类（根据年龄分组）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SexStatisticsExportVO extends PandaStatisticsExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0, value = "性别分组", converter = SexConverter.class)
    private Integer sex;
}
