package com.nbb.grpc.consumer.service;

import com.nbb.grpc.api.hello.HelloDTO;
import com.nbb.grpc.api.hello.HelloServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * @author 胡鹏
 */
@Service
public class HelloClientService {

    /** 注入gRPC客户端Stub，"provider-server"对应服务名（可在配置中指定） */
    @GrpcClient("provider-server")
    HelloServiceGrpc.HelloServiceBlockingStub helloServiceStub;


    public String hello(String name) {
        // 构建请求消息
        HelloDTO helloDTO = HelloDTO.newBuilder().setName(name).build();
        // 调用远程方法
        return helloServiceStub.sayHello(helloDTO).getMessage();
    }

    public String helloAgain(String name) {
        HelloDTO helloDTO = HelloDTO.newBuilder().setName(name).build();
        return helloServiceStub.sayHelloAgain(helloDTO).getMessage();
    }
}
