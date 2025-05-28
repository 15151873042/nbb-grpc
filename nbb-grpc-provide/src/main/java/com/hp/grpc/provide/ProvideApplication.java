package com.hp.grpc.provide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

/**
 * @author 胡鹏
 */
@SpringBootApplication
public class ProvideApplication {

    public static void main(String[] args) throws InterruptedException {
        // 启动 Spring Boot 应用
        SpringApplication.run(ProvideApplication.class, args);
    }

}
