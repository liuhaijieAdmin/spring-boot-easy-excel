package com.zhuzi.demo;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.ListUtils;
import com.zhuzi.listener.CommonListener;
import com.zhuzi.model.excel.PandaReadModel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简单案例
 */
@Slf4j
public class SimpleDemo {

    public static void readDemo() {
        String fileName = "熊猫基本信息集合-1723992183913.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        ZhuZiListener zhuZiListener = new ZhuZiListener();
        EasyExcel.read(fileName, ZhuZi.class, zhuZiListener).sheet().doRead();
        List<ZhuZi> zhuZis = zhuZiListener.getData();
        System.out.println("读取excel文件结束，总计解析到" + zhuZis.size() + "条数据！");
    }

    public static void writeDemo() {
        List<ZhuZi> zhuZis = new ArrayList<>();
        ZhuZi zhuZi = new ZhuZi(1L, "竹子", "男", "熊猫", new Date());
        zhuZis.add(zhuZi);

        // 可以写绝对路径，没有绝对路径默认放在当前目录下
        String fileName = "竹子数据-" + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(fileName, ZhuZi.class).sheet("竹子数据").doWrite(zhuZis);
    }

    public static void readDemo2() {
        String fileName = "D:\\Users\\Desktop\\111\\熊猫数据导入模板-V1.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        CommonListener<PandaReadModel> listener = new CommonListener<>(PandaReadModel.class);
        EasyExcel.read(fileName, PandaReadModel.class, listener).sheet().doRead();
        List<PandaReadModel> data = listener.getData();
        System.out.println("读取excel文件结束，总计解析到" + data + "条数据！");
    }

    public static void main(String[] args) {
        readDemo2();
    }
}
