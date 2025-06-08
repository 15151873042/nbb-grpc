package com.hp.grpc.consume.config;

import com.hp.grpc.api.ChatServiceGrpc;
import com.hp.grpc.api.ChatServiceGrpc.ChatServiceStub;
import com.hp.grpc.api.UserServiceGrpc;
import com.hp.grpc.api.UserServiceGrpc.UserServiceBlockingStub;
import com.hp.grpc.consume.interceptor.MyInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import io.grpc.NameResolver.Args;
import io.grpc.NameResolver.Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * @author 胡鹏
 */
@Configuration
public class GrpcConfig {

    @Autowired
    private MyInterceptor interceptor;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress("127.0.0.1", 8888)
                .intercept(interceptor)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserServiceBlockingStub userService() {
        ManagedChannel managedChannel = this.managedChannel();

        return UserServiceGrpc.newBlockingStub(managedChannel);
    }

    @Bean
    public ChatServiceStub chatService() {
        ManagedChannel managedChannel = this.managedChannel();
        return ChatServiceGrpc.newStub(managedChannel);
    }
}
