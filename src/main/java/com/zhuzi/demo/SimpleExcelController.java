package com.zhuzi.demo;

import com.alibaba.excel.EasyExcel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简易版excel导入/导出实现
 */
@RestController
@RequestMapping("/excel")
public class SimpleExcelController {
    @PostMapping("/import")
    public void excelImport(MultipartFile file) {
        if (null == file) {
            throw new RuntimeException("哎呀，网络出小差啦……");
        }
        ZhuZiListener listener = new ZhuZiListener();
        try {
            // 从上传的文件中获取流，并基于流读取excel文件的数据
            EasyExcel.read(file.getInputStream(), ZhuZi.class, listener).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException("Excel文件解析失败，请稍后重试~");
        }
        // 这里会获取到excel里的所有数据行，可以基于该数据进行校验、落库等业务操作
        List<ZhuZi> excelData = listener.getData();
    }

    @GetMapping("/export")
    public void excelExport(HttpServletResponse response) {
        // 内容类型也可以设置成
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "竹子数据-" + System.currentTimeMillis() + ".xlsx";
        try {
            // 防止中文乱码，使用URLEncoder重新编码（前端需要解码才能正常显示）
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Excel导出失败，请稍后重试");
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        // 这里模拟从库里读取的集合数据
        List<ZhuZi> zhuZis = new ArrayList<>();
        ZhuZi zhuZi = new ZhuZi(1L, "竹子", "男", "熊猫", new Date());
        zhuZis.add(zhuZi);

        try {
            // 这里指定excel数据的输出目标为响应流
            EasyExcel.write(response.getOutputStream(), ZhuZi.class).sheet().doWrite(zhuZis);
        } catch (IOException e) {
            throw new RuntimeException("Excel导出失败，请稍后重试");
        }
    }
}
