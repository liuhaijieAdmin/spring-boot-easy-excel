package com.zhuzi.demo;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.zhuzi.entity.Panda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Excel模板填充案例
 */
public class TemplateFillDemo {

    /*
    * 基于对象填充数据
    * */
    private static void simpleFillV1() {
        String templatePath = "excel/simple_template.xlsx";

        Panda panda = new Panda();
        panda.setName("竹子");
        panda.setNickname("小竹");
        panda.setUniqueCode("P888888");
        panda.setAddress("地球村888号");
        panda.setHeight(new BigDecimal("188.88"));
        panda.setMotto("今天的事能拖就拖，明天的事明天再说！");
        String fileName1 = "panda_info_entity.xlsx";
        EasyExcelFactory.write(fileName1).withTemplate(templatePath).sheet().doFill(panda);
    }

    /*
    * 基于Map填充数据
    * */
    private static void simpleFillV2() {
        String templatePath = "excel/simple_template.xlsx";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "竹子");
        dataMap.put("nickname", "小竹");
        dataMap.put("uniqueCode", "P888888");
        dataMap.put("address", "地球村888号");
        dataMap.put("height", new BigDecimal("188.88"));
        dataMap.put("motto", "今天的事能拖就拖，明天的事明天再说！");
        String fileName2 = "panda_info_map.xlsx";
        EasyExcelFactory.write(fileName2).withTemplate(templatePath).sheet().doFill(dataMap);
    }

    /*
    * 一次性将所有数据行都填充到模板
    * */
    private static void listFillV1() {
        List<Panda> pandas = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Panda panda = new Panda();
            panda.setName("竹子" + i + "号");
            panda.setUniqueCode("P" + i);
            panda.setBirthday(new Date());
            panda.setAddress("地球村" + i + "号");
            pandas.add(panda);
        }
        String fileName = "pandas_v1.xlsx";
        String templatePath = "excel/list_template.xlsx";
        EasyExcelFactory.write(fileName).withTemplate(templatePath).sheet().doFill(pandas);
    }

    /*
    * 分批次填充数据至模板
    * */
    private static void listFillV2() {
        String fileName = "pandas_v2.xlsx";
        String templatePath = "excel/list_template.xlsx";

        // 先创建一个Excel工作簿写对象、Sheet写对象
        ExcelWriter excelWriter = EasyExcelFactory.write(fileName).withTemplate(templatePath).build();
        WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
        // 模拟多次填充（填充五次，每次填充一条数据）
        for (int i = 1; i <= 5; i++) {
            List<Panda> pandas = new ArrayList<>();
            Panda panda = new Panda();
            panda.setName("竹子" + i + "号");
            panda.setUniqueCode("P" + i);
            panda.setBirthday(new Date());
            panda.setAddress("地球村" + i + "号");
            pandas.add(panda);
            // 开始触发数据填充
            excelWriter.fill(pandas, writeSheet);
        }
        // 手动创建的写对象记得手动关
        excelWriter.finish();
    }

    /*
    * 普通数据、列表数据混合填充场景
    * */
    private static void mixedFill() {
        // 导出的位置、模板的位置
        String fileName = "mixed_data.xlsx";
        String templatePath = "excel/mixed_template.xlsx";

        // 提前创建excel写对象
        ExcelWriter excelWriter = EasyExcelFactory.write(fileName).withTemplate(templatePath).build();
        WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        // 初始化熊猫列表数据
        List<Panda> pandas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i <= 5; i++) {
            Panda panda = new Panda();
            panda.setName("竹子" + i + "号");
            panda.setUniqueCode("P" + i);
            // 对熊猫身高求和
            BigDecimal height = new BigDecimal(i * 15.5);
            total = total.add(height);
            panda.setHeight(height);
            panda.setAddress("地球村" + i + "号");
            pandas.add(panda);
        }

        // 组装普通的数据（这里用了Map，不嫌麻烦也可以定义单独的类）
        BigDecimal avg = total.divide(new BigDecimal(pandas.size()), RoundingMode.HALF_UP);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("username", "竹子爱熊猫");
        dataMap.put("date", new Date());
        dataMap.put("totalHeight", total);
        dataMap.put("avgHeight", avg);

        // 写入熊猫集合数据、普通数据
        excelWriter.fill(pandas, fillConfig, writeSheet);
        excelWriter.fill(dataMap, writeSheet);
        excelWriter.finish();
    }

    /*
    * 横向列表填充
    * */
    private static void transverseListFill() {
        String fileName = "transverse_list_data.xlsx";
        String templatePath = "excel/transverse_list_template.xlsx";

        // 构建需要填充的数据
        List<DutyPanda> dutyPandas = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            DutyPanda dutyPanda = new DutyPanda();
            dutyPanda.setWeek("周" + i);
            dutyPanda.setName("竹子" + i + "号");
            dutyPandas.add(dutyPanda);
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("date", new Date());

        // 提前创建excel写对象
        ExcelWriter excelWriter = EasyExcelFactory.write(fileName).withTemplate(templatePath).build();
        WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
        // 设置填充模式为水平填充
        FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        excelWriter.fill(dutyPandas, fillConfig, writeSheet);
        excelWriter.fill(dataMap, writeSheet);
        excelWriter.finish();
    }

    /*
    * 多列表数据填充
    * */
    private static void manyListFill() {
        String fileName = "many_list_data.xlsx";
        String templatePath = "excel/many_list_template.xlsx";

        // 构建需要填充的数据
        List<DutyPanda> dutyPandas = new ArrayList<>();
        List<Panda> pandas = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String name = "竹子" + i + "号";
            if (i <= 5) {
                DutyPanda dutyPanda = new DutyPanda();
                dutyPanda.setWeek("周" + i);
                dutyPanda.setName(name);
                dutyPandas.add(dutyPanda);
            } else {
                Panda panda = new Panda();
                panda.setName(name);
                panda.setUniqueCode("P" + i);
                panda.setAddress("地球村" + i + "号");
                pandas.add(panda);
            }
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("date", new Date());
        dataMap.put("username", "竹子爱熊猫");
        dataMap.put("remark", "最终解释权归分配人所有！");

        // 提前创建excel写对象
        ExcelWriter excelWriter = EasyExcelFactory.write(fileName).withTemplate(templatePath).build();
        WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
        // 设置填充模式为水平填充
        FillConfig config1 = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        // 设置自动创建新行填充
        FillConfig config2 = FillConfig.builder().forceNewRow(true).build();
        excelWriter.fill(new FillWrapper("list1", dutyPandas), config1, writeSheet);
        excelWriter.fill(new FillWrapper("list2", pandas), config2, writeSheet);
        excelWriter.fill(dataMap, writeSheet);
        excelWriter.finish();
    }

    public static void main(String[] args) {
        manyListFill();
    }

}
