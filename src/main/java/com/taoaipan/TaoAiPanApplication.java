package com.taoaipan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/5 9:39
 * @description 主类
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.taoaipan"})
@MapperScan(basePackages = {"com.taoaipan.mappers"})
public class TaoAiPanApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaoAiPanApplication.class, args);
    }
}
