package com.nbb.grpc.client.controller;

import com.nbb.grpc.serverapi.ChatDTO;
import com.nbb.grpc.serverapi.ChatServiceGrpc.ChatServiceStub;
import com.nbb.grpc.serverapi.ChatVO;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡鹏
 */
@Slf4j
@RestController
public class ChatClientController implements InitializingBean {

    @Autowired
    private ChatServiceStub chatService;

    private StreamObserver<ChatDTO> requestObserver;
    private StreamObserver<ChatVO> responseObserver;


    @RequestMapping("/chat/send-message-to-server")
    public String sendMessageToServer(String message) {
        // 发送消息给服务端
        ChatDTO dto = ChatDTO.newBuilder().setMessage(message).build();
        requestObserver.onNext(dto);
        return "success";
    }


    @Override
    public void afterPropertiesSet() {
        this.responseObserver = this.newResponseObserverInstance();
        this.requestObserver = chatService.receiveMessage(responseObserver);
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
            }

            @Override
            public void onCompleted() {
                log.info("服务端完成响应");
            }
        };
    }
}
