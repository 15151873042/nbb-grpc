package io.github.nbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 胡鹏
 */
@SpringBootApplication
public class NbbGrpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbbGrpcServerApplication.class, args);
    }
}
