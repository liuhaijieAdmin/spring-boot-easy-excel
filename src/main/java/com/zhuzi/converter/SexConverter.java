package com.zhuzi.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.zhuzi.enums.Sex;

/**
 * 性别转换器
 */
public class SexConverter implements Converter<Integer> {
    @Override
    public WriteCellData<?> convertToExcelData(Integer code, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 将库里存储的code转换为具体的性别
        String value = Sex.valueOfCode(code);
        return new WriteCellData<>(value);
    }
}
