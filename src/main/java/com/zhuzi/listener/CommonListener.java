package com.zhuzi.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.zhuzi.util.ExcelUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
* Excel通用读取监听器
* */
public class CommonListener<T> extends AnalysisEventListener<T> {


    //创建list集合封装最终的数据
    private final List<T> data;

    // 字段列表
    private final Field[] fields;
    private final Class<T> clazz;
    private boolean validateSwitch = true;
    // 有效字段集合（低版本用于校验空数据行 ）
    // private List<Field> validFields;

    public CommonListener(Class<T> clazz) {
        fields = clazz.getDeclaredFields();
        this.clazz = clazz;
        this.data = new ArrayList<T>();
    }

    /*
    * 每解析到一行数据都会触发
    * */
    @Override
    public void invoke(T row, AnalysisContext analysisContext) {
        // 忽略所有字段为空的数据行（低版本EasyExcel请开启）
//         if (ExcelUtil.rowIsNull(row, validFields)) {
//             return;
//         }
        data.add(row);
    }

    /*
    * 读取到excel头信息时触发，会将表头数据转为Map集合
    * */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if (validateSwitch) {
            ExcelUtil.validateExcelTemplate(headMap, clazz, fields);
        }
    }

    /*
     * 所有数据解析完之后触发
     * */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }

    /*
     * 关闭excel表头验证
     * */
    public void offValidate() {
        this.validateSwitch = false;
    }

    public List<T> getData() {
        return data;
    }
}
