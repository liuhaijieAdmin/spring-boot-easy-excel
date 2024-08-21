package com.zhuzi.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 熊猫统计导出VO类
 */
@Data
public class PandaStatisticsExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 1, value = "熊数")
    private Integer counting;

    @ExcelProperty(index = 2, value = "身高>=170cm熊数")
    private Integer heightGte170cm;
}
