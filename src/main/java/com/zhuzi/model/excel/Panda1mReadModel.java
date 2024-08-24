package com.zhuzi.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 熊猫一百万数据的读取模型类
 */
@Data
public class Panda1mReadModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("编码")
    private String uniqueCode;

    @ExcelProperty("性别")
    private Integer sex;

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
