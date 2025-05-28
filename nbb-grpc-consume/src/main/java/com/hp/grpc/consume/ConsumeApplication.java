package com.hp.grpc.consume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 胡鹏
 */
@SpringBootApplication
public class ConsumeApplication {

    public static void main(String[] args) throws InterruptedException {
        // 启动 Spring Boot 应用
        SpringApplication.run(ConsumeApplication.class, args);
    }

}
