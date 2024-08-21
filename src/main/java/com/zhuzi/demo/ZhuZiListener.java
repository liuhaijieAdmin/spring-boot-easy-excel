package com.zhuzi.demo;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 竹子数据-Excel数据读取监听器
 */
@Slf4j
public class ZhuZiListener extends AnalysisEventListener<ZhuZi> {

    private final List<ZhuZi> data = new ArrayList<>();

    /*
    * 每解析一条数据都会触发一次invoke()方法
    *
    * */
    @Override
    public void invoke(ZhuZi zhuZi, AnalysisContext analysisContext) {
        log.info("成功解析到一条数据：{}", zhuZi);
        data.add(zhuZi);
    }

    /*
    * 当一个excel文件所有数据解析完成后，会触发此方法
    * */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据都已解析完毕！");
    }

    public List<ZhuZi> getData() {
        return data;
    }
}
