package com.nbb.grpc.consumer.service;

import com.nbb.grpc.api.serverPush.PushDTO;
import com.nbb.grpc.api.serverPush.PushVO;
import com.nbb.grpc.api.serverPush.ServerPushServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

/**
 * @author 胡鹏
 */
@Slf4j
@Service
public class ServerPushClientService {

    @GrpcClient("provider-server")
    private ServerPushServiceGrpc.ServerPushServiceStub pushStub;

    /**
     * 建立连接并接收服务端推送（客户端不发送数据）
     */
    public void startReceivingPush()  {
        CountDownLatch latch = new CountDownLatch(1);

        // 1. 建立双向流连接
        StreamObserver<PushDTO> requestObserver = pushStub.subscribePush(new StreamObserver<PushVO>() {
            @Override
            public void onNext(PushVO vo) {
                // 2. 接收服务端推送的消息并处理
                log.info("收到服务端推送：{}", vo.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                log.error("推送接收异常", t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("服务端关闭推送");
                latch.countDown();
            }
        });

        // 3. 客户端可选：发送1条空消息确认连接（不发送也可正常接收推送）
        // requestObserver.onNext(PushDTO.newBuilder().build());

        // 4. 保持连接（实际业务中可根据需求设置超时或手动关闭）
//        latch.await();
        // 如需主动关闭，调用：requestObserver.onCompleted();
    }
}
