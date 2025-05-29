package com.hp.grpc.consume.controller;

import com.hp.grpc.api.ChatDTO;
import com.hp.grpc.api.ChatServiceGrpc.ChatServiceStub;
import com.hp.grpc.api.ChatVO;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author 胡鹏
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController implements InitializingBean {

    @Autowired
    private ChatServiceStub chatService;

    private StreamObserver<ChatDTO> requestObserver;
    private StreamObserver<ChatVO> responseObserver;

    private Executor executor = Executors.newSingleThreadExecutor();


    @RequestMapping("")
    public String chat(String message) {
        // 发送消息给服务端
        ChatDTO dto = ChatDTO.newBuilder()
                .setMessage(message)
                .build();
        requestObserver.onNext(dto);
        return "a";
    }

    @Override
    public void afterPropertiesSet() {
        this.responseObserver = this.newResponseObserverInstance();
        this.requestObserver = chatService.sendMessage(responseObserver);
        System.out.println("abc");
    }

    private StreamObserver<ChatVO> newResponseObserverInstance() {
        return new StreamObserver<ChatVO>() {
            @Override
            public void onNext(ChatVO vo) {
                log.info("收到服务端响应：{}", vo.getMessage());

            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(), t);
                reConnect();
            }

            @Override
            public void onCompleted() {
                log.info("服务端完成响应");
            }
        };
    }

    private void reConnect() {
        try {
            Thread.sleep(5000L);
            this.requestObserver = chatService.sendMessage(responseObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
