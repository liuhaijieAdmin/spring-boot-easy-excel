package com.zhuzi.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.zhuzi.converter.SexConverter;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 熊猫导出VO类
 */
@Data
@ColumnWidth(10)
public class PandaExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "熊猫昵称", index = 0)
    private String nickname;

    @ExcelProperty(value = "性别", index = 1, converter = SexConverter.class)
    private Integer sex;

    @ExcelProperty(value = "唯一编码", index = 2)
    private String uniqueCode;

    @ExcelProperty(value = "身高", index = 3)
    private BigDecimal height;

    @ExcelProperty(value = "出生日期", index = 4)
    @DateTimeFormat("yyyy-MM-dd")
    @ColumnWidth(15)
    private Date birthday;

    @ExcelProperty(value = "创建时间", index = 5)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(20)
    private Date createTime;
}
