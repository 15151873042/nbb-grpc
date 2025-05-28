package com.hp.grpc.provide.config;

import com.hp.grpc.provide.rpc.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author 胡鹏
 */
@Configuration
public class GrpcConfig {


    @Bean
    public Server grpcServer(final UserServiceImpl userService) throws IOException {
        Server server = ServerBuilder
                .forPort(8888)
                .addService(userService)
                .build();
        server.start();
        return server;
    }
}
