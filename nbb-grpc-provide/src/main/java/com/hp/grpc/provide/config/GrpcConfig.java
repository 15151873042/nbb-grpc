package com.hp.grpc.provide.config;

import com.hp.grpc.provide.interceptor.GrpcConnectionInterceptor;
import com.hp.grpc.provide.rpc.ChatServiceImpl;
import com.hp.grpc.provide.rpc.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author 胡鹏
 */
@Configuration
public class GrpcConfig {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ChatServiceImpl chatService;
    @Autowired
    private GrpcConnectionInterceptor interceptor;


    @Bean
    public Server grpcServer() throws IOException {

        Server server = ServerBuilder
                .forPort(8888)
                .addService(userService)
                .addService(chatService)
                .intercept(interceptor)
                .build();

        server.start();
        return server;
    }
}
