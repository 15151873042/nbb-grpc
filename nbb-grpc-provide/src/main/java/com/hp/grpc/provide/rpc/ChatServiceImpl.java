package com.hp.grpc.provide.rpc;

import cn.hutool.core.date.DateUtil;
import com.hp.grpc.api.ChatDTO;
import com.hp.grpc.api.ChatServiceGrpc;
import com.hp.grpc.api.ChatVO;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author 胡鹏
 */

@Slf4j
@Service
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase implements InitializingBean {


    private List<StreamObserver<ChatVO>> responseObserverList = new CopyOnWriteArrayList<>();


    @Override
    public StreamObserver<ChatDTO> sendMessage(StreamObserver<ChatVO> responseObserver) {
        responseObserverList.add(responseObserver);

        return new StreamObserver<ChatDTO>() {
            @Override
            public void onNext(ChatDTO dto) {
                // 处理客户端发送的消息
                log.info("收到客户端消息：{}", dto.getMessage());

                // 向客户端发送响应
                ChatVO vo = ChatVO.newBuilder().setMessage("服务端收到：" + dto.getMessage()).build();
                responseObserver.onNext(vo);
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(), t);
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                // 客户端发送完成
                log.info("完成客户端消息发送");
                responseObserver.onCompleted();
            }
        };
    }



    @Override
    public void afterPropertiesSet() {
        // 创建一个单线程的定时任务执行器
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            for (StreamObserver<ChatVO> responseObserver : responseObserverList) {
                ChatVO vo = ChatVO.newBuilder().setMessage("服务端主动推送消息啦！！！ + " + DateUtil.now()).build();

                responseObserver.onNext(vo);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
