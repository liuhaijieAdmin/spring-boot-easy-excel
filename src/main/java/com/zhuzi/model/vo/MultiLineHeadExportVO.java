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
 * 多行头导出VO类
 */
@Data
public class MultiLineHeadExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = {"基本信息", "昵称"})
    private String nickname;

    @ExcelProperty(value = {"基本信息", "编码"})
    private String uniqueCode;

    @ExcelProperty(value = {"基本信息","性别"}, converter = SexConverter.class)
    private Integer sex;

    @ExcelProperty(value = {"基本信息","身高"})
    private BigDecimal height;

    @ExcelProperty(value = {"基本信息","出生日期"})
    @DateTimeFormat("yyyy-MM-dd")
    @ColumnWidth(15)
    private Date birthday;

    @ExcelProperty(value = {"其他信息","等级"})
    private String level;

    @ExcelProperty(value = {"其他信息","座右铭"})
    @ColumnWidth(30)
    private String motto;

    @ExcelProperty(value = {"其他信息", "创建时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(20)
    private Date createTime;
}
