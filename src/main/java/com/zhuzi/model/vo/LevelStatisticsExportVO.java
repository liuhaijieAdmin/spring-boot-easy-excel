package com.zhuzi.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 熊猫统计导出VO类（根据等级分组）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LevelStatisticsExportVO extends PandaStatisticsExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0, value = "等级分组")
    private String level;
}
