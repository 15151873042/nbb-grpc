package com.nbb.grpc.provider.service;

import com.nbb.grpc.api.serverPush.PushDTO;
import com.nbb.grpc.api.serverPush.PushVO;
import com.nbb.grpc.api.serverPush.ServerPushServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
@Slf4j
@GrpcService
public class ServerPushService extends ServerPushServiceGrpc.ServerPushServiceImplBase{

    @Override
    public StreamObserver<PushDTO> subscribePush(StreamObserver<PushVO> responseObserver) {
        log.info("客户端已建立连接，开始推送数据...");

        // 1. 启动定时任务，向客户端主动推送数据（示例：每2秒推送一次）
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            // 构建推送消息
            PushVO vo = PushVO.newBuilder()
                    .setMessage("服务端实时数据：" + System.currentTimeMillis())
                    .build();
            // 推送消息给客户端
            responseObserver.onNext(vo);
            log.info("已推送消息：{}", vo.getMessage());
        }, 0, 2, TimeUnit.SECONDS); // 立即开始，每2秒一次

        // 2. 返回客户端消息处理器（即使客户端不发消息，也需实现）
        return new StreamObserver<PushDTO>() {
            @Override
            public void onNext(PushDTO dto) {
                // 客户端可能发送空消息（可选），这里仅日志记录
                log.info("收到客户端空消息（保持连接）");
            }

            @Override
            public void onError(Throwable t) {
                log.error("客户端连接异常", t);
                scheduler.shutdown(); // 停止推送任务
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                log.info("客户端主动关闭连接");
                scheduler.shutdown(); // 停止推送任务
                responseObserver.onCompleted(); // 结束流
            }
        };
    }
}
