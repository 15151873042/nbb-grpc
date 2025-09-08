package com.nbb.grpc.consumer.service;

import com.nbb.grpc.api.stream.StreamDTO;
import com.nbb.grpc.api.stream.StreamServiceGrpc;
import com.nbb.grpc.api.stream.StreamVO;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
@Slf4j
@Service
public class StreamClientService {


    /**
     * 注入gRPC客户端Stub，"provider-server"对应服务名（可在配置中指定）
     */
    @GrpcClient("provider-server")
    private StreamServiceGrpc.StreamServiceStub streamServiceStub; // 异步Stub（用于流操作）

    /**
     * 调用服务端流
     */
    public void callServerStream(String content, int count) throws InterruptedException {
        log.info("开始调用服务端流：内容={}，请求{}条消息", content, count);

        // 构建请求
        StreamDTO dto = StreamDTO.newBuilder()
                .setContent(content)
                .setCount(count)
                .build();

        // 用于等待流结束的计数器
        CountDownLatch latch = new CountDownLatch(1);

        // 调用服务端流（异步）
        streamServiceStub.serverStream(dto, new StreamObserver<StreamVO>() {
            @Override
            public void onNext(StreamVO vo) {
                // 接收服务端的单个响应
                log.info("收到服务端流消息[{}]：{}", vo.getSequence(), vo.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                log.error("服务端流调用错误", t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("服务端流调用完成");
                latch.countDown(); // 流结束，释放等待
            }
        });

        // 等待流处理完成（最多等30秒）
        latch.await(30, TimeUnit.SECONDS);
    }

    /**
     * 调用客户端流
     */
    public void callClientStream(List<String> messages) throws InterruptedException {
        log.info("开始调用客户端流：共{}条消息", messages.size());

        CountDownLatch latch = new CountDownLatch(1);

        // 调用客户端流，获取用于发送消息的StreamObserver
        StreamObserver<StreamDTO> requestObserver = streamServiceStub.clientStream(new StreamObserver<StreamVO>() {
            @Override
            public void onNext(StreamVO vo) {
                // 接收服务端的最终响应
                log.info("收到客户端流汇总结果[{}]：{}", vo.getSequence(), vo.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                log.error("客户端流调用错误", t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("客户端流调用完成");
                latch.countDown();
            }
        });

        // 客户端分多次发送消息
        for (int i = 0; i < messages.size(); i++) {
            try {
                // 模拟发送间隔
                TimeUnit.MILLISECONDS.sleep(400);

                StreamDTO dto = StreamDTO.newBuilder()
                        .setContent(messages.get(i))
                        .build();
                requestObserver.onNext(dto);
                log.info("客户端流发送第{}条消息：{}", i + 1, messages.get(i));
            } catch (Exception e) {
                log.error("发送客户端流消息失败", e);
                requestObserver.onError(e);
                return;
            }
        }

        // 客户端流发送完成（必须调用，否则服务端会一直等待）
        requestObserver.onCompleted();

        // 等待服务端响应
        latch.await(30, TimeUnit.SECONDS);
    }

    /**
     * 调用双向流
     */
    public void callBidirectionalStream(List<String> messages) throws InterruptedException {
        log.info("开始调用双向流：共{}条消息", messages.size());

        CountDownLatch latch = new CountDownLatch(1);

        // 调用双向流，获取用于发送消息的StreamObserver
        StreamObserver<StreamDTO> requestObserver = streamServiceStub.bidirectionalStream(new StreamObserver<StreamVO>() {
            @Override
            public void onNext(StreamVO vo) {
                // 接收服务端的实时响应
                log.info("收到双向流服务端消息[{}]：{}", vo.getSequence(), vo.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                log.error("双向流调用错误", t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("双向流调用完成");
                latch.countDown();
            }
        });

        // 客户端分多次发送消息（服务端会实时响应）
        for (int i = 0; i < messages.size(); i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(600); // 模拟用户输入间隔

                StreamDTO dto = StreamDTO.newBuilder()
                        .setContent(messages.get(i))
                        .build();
                requestObserver.onNext(dto);
                log.info("双向流客户端发送第{}条消息：{}", i + 1, messages.get(i));
            } catch (Exception e) {
                log.error("发送双向流消息失败", e);
                requestObserver.onError(e);
                return;
            }
        }

        // 客户端结束发送
        requestObserver.onCompleted();

        // 等待流结束
        latch.await(30, TimeUnit.SECONDS);
    }

}
