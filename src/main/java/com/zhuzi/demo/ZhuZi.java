package com.zhuzi.demo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 竹子实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ColumnWidth(30)
public class ZhuZi {
    @ExcelProperty("编号")
    private Long id;
    @ExcelProperty("名称")
    private String name;
    @ExcelProperty("性别")
    private String sex;
    @ExcelProperty("爱好")
    private String hobby;
    @ExcelProperty("生日")
    private Date birthday;
}