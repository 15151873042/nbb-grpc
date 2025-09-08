package com.nbb.grpc.provider.service;

import com.nbb.grpc.api.hello.HelloDTO;
import com.nbb.grpc.api.hello.HelloServiceGrpc;
import com.nbb.grpc.api.hello.HelloVO;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author 胡鹏
 */
@GrpcService
public class HelloService extends HelloServiceGrpc.HelloServiceImplBase{

    @Override
    public void sayHello(HelloDTO request, StreamObserver<HelloVO> responseObserver) {
        // 构建响应对象
        HelloVO helloVO = HelloVO.newBuilder()
                .setMessage("Hello, " + request.getName() + "!")
                .build();

        // 发送响应并结束
        responseObserver.onNext(helloVO);

        responseObserver.onCompleted();
    }
    @Override
    public void sayHelloAgain(HelloDTO request, StreamObserver<HelloVO> responseObserver) {
        // 构建响应对象
        HelloVO helloVO = HelloVO.newBuilder()
                .setMessage("Hello again, " + request.getName() + " !")
                .build();

        // 发送响应并结束
        responseObserver.onNext(helloVO);
        responseObserver.onCompleted();
    }
}
