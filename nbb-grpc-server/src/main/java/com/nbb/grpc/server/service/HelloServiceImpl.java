package com.nbb.grpc.server.service;

import com.nbb.grpc.server.interceptor.ClientIdInterceptor;
import com.nbb.grpc.serverapi.HelloRequest;
import com.nbb.grpc.serverapi.HelloResponse;
import com.nbb.grpc.serverapi.HelloServiceGrpc;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 胡鹏
 */
@Service
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    // 1. 简单 RPC 实现
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        // 1. 接收客户端请求参数
        String name = request.getName();
        int age = request.getAge();

        // 2. 业务逻辑处理
        String message = "Hello, " + name + "! You are " + age + " years old.";

        // 3. 构建响应并返回
        HelloResponse response = HelloResponse.newBuilder()
                .setMessage(message)
                .build();

        // 发送响应给客户端
        responseObserver.onNext(response);
        // 标识响应完成（必须调用）
        responseObserver.onCompleted();
    }

    // 2. 服务端流式 RPC 实现
    @Override
    public void sayHelloStreamServer(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String name = request.getName();
        // 模拟返回多个响应（如实时推送 3 条消息）
        for (int i = 1; i <= 3; i++) {
            HelloResponse response = HelloResponse.newBuilder()
                    .setMessage("Hello " + name + " - Stream " + i)
                    .build();
            // 多次调用 onNext 发送多个响应
            responseObserver.onNext(response);
            try {
                Thread.sleep(1000); // 模拟延迟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 流结束
        responseObserver.onCompleted();
    }

    // 3. 客户端流式 RPC 实现
    @Override
    public StreamObserver<HelloRequest> sayHelloStreamClient(StreamObserver<HelloResponse> responseObserver) {
        // 存储客户端发送的多个请求
        List<String> names = new ArrayList<>();

        // 返回 StreamObserver，处理客户端的流请求
        return new StreamObserver<HelloRequest>() {
            // 客户端每发一个请求，触发一次 onNext
            @Override
            public void onNext(HelloRequest request) {
                names.add(request.getName());
            }

            // 客户端流发生错误时触发
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            // 客户端流结束时触发，返回最终响应
            @Override
            public void onCompleted() {
                String message = "Hello, " + String.join(", ", names) + "!";
                HelloResponse response = HelloResponse.newBuilder()
                        .setMessage(message)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    // 4. 双向流式 RPC 实现
    @Override
    public StreamObserver<HelloRequest> sayHelloStreamBidirectional(StreamObserver<HelloResponse> responseObserver) {

        // 直接从 Context 取，无需再解析 Metadata
        String clientId = ClientIdInterceptor.CLIENT_ID_CONTEXT_KEY.get(Context.current());

        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest request) {
                // 客户端每发一个请求，服务端立即回一个响应
                String clientId = ClientIdInterceptor.CLIENT_ID_CONTEXT_KEY.get(Context.current());
                String message = "Hello, " + request.getName() + " (Bidirectional) " + clientId;
                HelloResponse response = HelloResponse.newBuilder()
                        .setMessage(message)
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
