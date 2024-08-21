package com.zhuzi.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 熊猫导出类
 */
@Data
public class PandaReadModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("外号")
    private String nickname;

    @ExcelProperty("唯一编码")
    private String uniqueCode;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("身高")
    private BigDecimal height;

    @ExcelProperty("出生日期")
    @DateTimeFormat("yyyy-MM-dd")
    private Date birthday;

    @ExcelProperty("等级")
    private String level;

    @ExcelProperty("座右铭")
    private String motto;

    @ExcelProperty("所在地址")
    private String address;
}
