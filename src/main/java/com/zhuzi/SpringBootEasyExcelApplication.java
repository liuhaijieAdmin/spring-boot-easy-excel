package com.zhuzi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zhuzi"})
@MapperScan(basePackages = {"com.zhuzi.mapper"})
public class SpringBootEasyExcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEasyExcelApplication.class, args);
    }

}
