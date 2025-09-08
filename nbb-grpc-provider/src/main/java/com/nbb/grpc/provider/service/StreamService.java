package com.nbb.grpc.provider.service;

import com.nbb.grpc.api.stream.StreamDTO;
import com.nbb.grpc.api.stream.StreamServiceGrpc;
import com.nbb.grpc.api.stream.StreamVO;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
@Slf4j
@GrpcService
public class StreamService extends StreamServiceGrpc.StreamServiceImplBase{


    /**
     * 1. 服务端流：客户端发送1次请求，服务端返回多个响应
     * 场景：客户端请求"推送N条消息"，服务端持续返回消息
     */
    @Override
    public void serverStream(StreamDTO dto, StreamObserver<StreamVO> responseObserver) {
        log.info("收到服务端流请求：{}，需要返回{}条消息", dto.getContent(), dto.getCount());

        // 模拟服务端分多次返回消息
        for (int i = 1; i <= dto.getCount(); i++) {
            try {
                // 模拟处理延迟（如数据库查询、消息生成）
                TimeUnit.MILLISECONDS.sleep(500);

                // 发送当前消息
                StreamVO response = StreamVO.newBuilder()
                        .setMessage("服务端流消息：" + dto.getContent() + " #" + i)
                        .setSequence(i)
                        .build();
                responseObserver.onNext(response);
                log.info("服务端流发送第{}条消息", i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onError(e); // 发生错误时通知客户端
                return;
            }
        }

        // 流结束（必须调用，否则客户端会一直等待）
        responseObserver.onCompleted();
        log.info("服务端流发送完成");
    }

    /**
     * 2. 客户端流：客户端发送多个请求，服务端返回1次响应
     * 场景：客户端上传多个数据片段，服务端汇总后返回结果
     */
    @Override
    public StreamObserver<StreamDTO> clientStream(StreamObserver<StreamVO> responseObserver) {
        log.info("开始接收客户端流请求...");

        // 用于收集客户端发送的所有消息
        List<String> clientMessages = new ArrayList<>();

        // 返回一个StreamObserver处理客户端的流消息
        return new StreamObserver<StreamDTO>() {
            @Override
            public void onNext(StreamDTO dto) {
                // 接收客户端发送的单个消息
                clientMessages.add(dto.getContent());
                log.info("收到客户端流消息：{}（累计{}条）", dto.getContent(), clientMessages.size());
            }

            @Override
            public void onError(Throwable t) {
                log.error("客户端流发生错误", t);
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                // 客户端流发送完成，汇总结果并返回
                String summary = "客户端共发送" + clientMessages.size() + "条消息：" + String.join("；", clientMessages);
                StreamVO response = StreamVO.newBuilder()
                        .setMessage(summary)
                        .setSequence(clientMessages.size())
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted(); // 结束响应
                log.info("客户端流处理完成，返回汇总结果");
            }
        };
    }

    /**
     * 3. 双向流：客户端和服务端互相发送多个消息
     * 场景：类似聊天功能，双方可实时互发消息
     */
    @Override
    public StreamObserver<StreamDTO> bidirectionalStream(StreamObserver<StreamVO> responseObserver) {
        log.info("双向流建立连接");

        // 返回一个StreamObserver处理客户端的消息，同时服务端也可主动发送消息
        return new StreamObserver<StreamDTO>() {
            private int clientMsgCount = 0; // 客户端消息计数器

            @Override
            public void onNext(StreamDTO dto) {
                clientMsgCount++;
                log.info("收到双向流客户端消息：{}（第{}条）", dto.getContent(), clientMsgCount);

                // 收到客户端消息后，立即回复一条消息（模拟实时交互）
                try {
                    // 模拟处理延迟
                    TimeUnit.MILLISECONDS.sleep(300);

                    StreamVO response = StreamVO.newBuilder()
                            .setMessage("服务端收到：" + dto.getContent() + "（已确认）")
                            .setSequence(clientMsgCount)
                            .build();
                    responseObserver.onNext(response);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("双向流发生错误", t);
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                // 客户端结束流时，服务端发送最终消息并结束
                StreamVO finalResponse = StreamVO.newBuilder()
                        .setMessage("双向流结束，共收到" + clientMsgCount + "条消息")
                        .setSequence(clientMsgCount)
                        .build();
                responseObserver.onNext(finalResponse);
                responseObserver.onCompleted();
                log.info("双向流结束");
            }
        };
    }
}
