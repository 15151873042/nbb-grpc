package com.hp.grpc.consume.config;

import com.hp.grpc.api.UserServiceGrpc;
import com.hp.grpc.api.UserServiceGrpc.UserServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 胡鹏
 */
@Configuration
public class GrpcConfig {

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress("127.0.0.1", 8888)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserServiceBlockingStub userService() {
        ManagedChannel managedChannel = this.managedChannel();

        return UserServiceGrpc.newBlockingStub(managedChannel);
    }
}
