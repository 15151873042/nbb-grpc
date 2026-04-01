package com.nbb.grpc.server.config;

import com.nbb.grpc.server.interceptor.ClientIdInterceptor;
import com.nbb.grpc.server.service.ChatServiceImpl;
import com.nbb.grpc.server.service.HelloServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
@Configuration
public class GrpcServerConfig {

    @Autowired
    private HelloServiceImpl helloService;
    @Autowired
    private ChatServiceImpl chatService;
    @Autowired
    private ClientIdInterceptor clientIdInterceptor;

    @Value("${grpc.port}")
    private Integer grpcPort;

    @Bean
    public Server businessGrpcServer() throws IOException {
        Server server = ServerBuilder
                .forPort(grpcPort)
                // 开启服务端 KeepAlive，5秒未收到客户端心跳，主动发 KeepAlive 探测
                .keepAliveTime(1, TimeUnit.SECONDS)
                // KeepAlive 探测超时时间
                .keepAliveTimeout(200, TimeUnit.MILLISECONDS)
                .addService(helloService)
                .addService(chatService)
                .intercept(clientIdInterceptor)
                .build();

        server.start();
        return server;
    }
}
