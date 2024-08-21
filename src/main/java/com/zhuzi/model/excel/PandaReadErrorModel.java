package com.zhuzi.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 熊猫导入类的回显对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PandaReadErrorModel extends PandaReadModel implements Serializable {
    private static final long serialVersionUID = 1L;

    // 显式将错误信息放到最后一列（第十列）
    @ExcelProperty(index = 9, value = "错误信息项")
    private String errorMsg;
}
