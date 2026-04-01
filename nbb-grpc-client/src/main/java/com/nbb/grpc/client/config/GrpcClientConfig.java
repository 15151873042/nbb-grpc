package com.nbb.grpc.client.config;

import com.nbb.grpc.client.interceptor.ClientIdClientInterceptor;
import com.nbb.grpc.serverapi.ChatServiceGrpc;
import com.nbb.grpc.serverapi.ChatServiceGrpc.ChatServiceStub;
import com.nbb.grpc.serverapi.HelloServiceGrpc;
import com.nbb.grpc.serverapi.HelloServiceGrpc.HelloServiceBlockingStub;
import com.nbb.grpc.serverapi.HelloServiceGrpc.HelloServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author 胡鹏
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc-server.host}")
    private String grpcServerHost;
    @Value("${grpc-server.port}")
    private Integer grpcServerPort;
    @Resource
    private ClientIdClientInterceptor clientIdInterceptor;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress(grpcServerHost, grpcServerPort)
                .intercept(clientIdInterceptor)
                .usePlaintext()
                .build();
    }

    /**
     * 创建阻塞 stub（同步调用，简单 RPC 推荐）
     * @return
     */
    @Bean
    public HelloServiceBlockingStub blockingHelloService(ManagedChannel managedChannel) {
        return HelloServiceGrpc.newBlockingStub(managedChannel);
    }

    /**
     * 创建异步 stub（流式 RPC 必须用异步）
     * @return
     */
    @Bean
    public HelloServiceStub asyncHelloService(ManagedChannel managedChannel) {
        return HelloServiceGrpc.newStub(managedChannel);
    }

    @Bean
    public ChatServiceStub chatServiceStub(ManagedChannel managedChannel) {
        return ChatServiceGrpc.newStub(managedChannel);
    }
}
