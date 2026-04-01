import com.nbb.grpc.serverapi.HelloRequest;
import com.nbb.grpc.serverapi.HelloResponse;
import com.nbb.grpc.serverapi.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class HelloServiceClient {

    public static void main(String[] args) throws InterruptedException {
        // 1. 建立与服务端的连接（通道）
        String host = "127.0.0.1";
        int port = 9100;
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // 开发环境禁用 SSL，生产环境需开启
                .build();


        // 2. 创建阻塞 stub（同步调用，简单 RPC 推荐）
        HelloServiceGrpc.HelloServiceBlockingStub blockingStub = HelloServiceGrpc.newBlockingStub(channel);

        // 3. 创建异步 stub（流式 RPC 必须用异步）
        HelloServiceGrpc.HelloServiceStub asyncStub = HelloServiceGrpc.newStub(channel);

        // ---------------- 测试 1：简单 RPC ----------------
        System.out.println("=== 简单 RPC 调用 ===");
        HelloRequest request = HelloRequest.newBuilder()
                .setName("张三")
                .setAge(20)
                .build();
        HelloResponse response = blockingStub.sayHello(request);
        System.out.println("服务端响应：" + response.getMessage());

        // ---------------- 测试 2：服务端流式 RPC ----------------
        System.out.println("\n=== 服务端流式 RPC 调用 ===");
        HelloRequest streamRequest = HelloRequest.newBuilder().setName("李四").build();
        // 迭代器遍历服务端返回的多个响应
        java.util.Iterator<HelloResponse> streamResponses = blockingStub.sayHelloStreamServer(streamRequest);
        // 2. 包装为 Iterable，即可使用 for-each
        Iterable<HelloResponse> responseIterable = () -> streamResponses;
        for (HelloResponse res : responseIterable) {
            System.out.println("服务端流式响应：" + res.getMessage());
        }

        // ---------------- 测试 3：客户端流式 RPC ----------------
        System.out.println("\n=== 客户端流式 RPC 调用 ===");
        StreamObserver<HelloResponse> clientStreamResponseObserver = new StreamObserver<HelloResponse>() {
            @Override
            public void onNext(HelloResponse value) {
                System.out.println("客户端流式最终响应：" + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("客户端流式 RPC 结束");
            }
        };
        // 发送多个请求
        StreamObserver<HelloRequest> clientStreamRequestObserver = asyncStub.sayHelloStreamClient(clientStreamResponseObserver);
        clientStreamRequestObserver.onNext(HelloRequest.newBuilder().setName("王五").build());
        clientStreamRequestObserver.onNext(HelloRequest.newBuilder().setName("赵六").build());
        clientStreamRequestObserver.onCompleted(); // 标识客户端流结束

        // 等待客户端流式 RPC 完成
        TimeUnit.SECONDS.sleep(2);

        // ---------------- 测试 4：双向流式 RPC ----------------
        System.out.println("\n=== 双向流式 RPC 调用 ===");
        StreamObserver<HelloResponse> bidirectionalResponseObserver = new StreamObserver<HelloResponse>() {
            @Override
            public void onNext(HelloResponse value) {
                System.out.println("双向流式响应：" + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("双向流式 RPC 结束");
            }
        };

        StreamObserver<HelloRequest> bidirectionalRequestObserver = asyncStub.sayHelloStreamBidirectional(bidirectionalResponseObserver);

        // 客户端发多个请求，服务端实时回响应
        bidirectionalRequestObserver.onNext(HelloRequest.newBuilder().setName("小明").build());
        bidirectionalRequestObserver.onNext(HelloRequest.newBuilder().setName("小红").build());
        bidirectionalRequestObserver.onCompleted();

        // 等待双向流式 RPC 完成
        TimeUnit.SECONDS.sleep(2);

        // 4. 关闭通道
        channel.shutdown();
    }
}
