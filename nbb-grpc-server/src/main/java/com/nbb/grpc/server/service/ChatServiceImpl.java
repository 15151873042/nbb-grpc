package com.nbb.grpc.server.service;

import cn.hutool.core.util.ObjUtil;
import com.nbb.grpc.server.interceptor.ClientIdInterceptor;
import com.nbb.grpc.serverapi.ChatDTO;
import com.nbb.grpc.serverapi.ChatServiceGrpc;
import com.nbb.grpc.serverapi.ChatVO;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 胡鹏
 */
@Slf4j
@Service
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase implements ChatService{

    /**
     * key为客户端clientId
     * value为向客户端推送数据的对象
     */
    private Map<String, StreamObserver<ChatVO>> client_id_2_response_observer = new ConcurrentHashMap<>();

    /**
     * 有客户端连接连入时，会调用改方法
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<ChatDTO> receiveMessage(StreamObserver<ChatVO> responseObserver) {
        String clientId = ClientIdInterceptor.CLIENT_ID_CONTEXT_KEY.get(Context.current());
        client_id_2_response_observer.put(clientId, responseObserver);
        log.info("客户端【{}】成功连入了", clientId);

        return new StreamObserver<ChatDTO>() {
            @Override
            public void onNext(ChatDTO dto) {
                // 客户端每发一个请求，改方法会被调用，服务端立即回一个响应
                String clientId = ClientIdInterceptor.CLIENT_ID_CONTEXT_KEY.get(Context.current());
                log.info("收到客户端【{}】,发送的消息【{}】", clientId, dto.getMessage());

                String responseMessage = "Hello, " + dto.getMessage();
                ChatVO vo = ChatVO.newBuilder().setMessage(responseMessage).build();
                responseObserver.onNext(vo);
            }

            @Override
            public void onError(Throwable t) {
                String clientId = ClientIdInterceptor.CLIENT_ID_CONTEXT_KEY.get(Context.current());
                log.info("客户端【{}】,触发error了", clientId);
                log.error(t.getMessage(), t);

                client_id_2_response_observer.remove(clientId);
            }

            @Override
            public void onCompleted() {
                String clientId = ClientIdInterceptor.CLIENT_ID_CONTEXT_KEY.get(Context.current());
                log.info("客户端【{}】,主动断开连接了", clientId);

                client_id_2_response_observer.remove(clientId);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public Set<String> listAllClientId() {
        return client_id_2_response_observer.keySet();
    }

    @Override
    public void pushMessageToClient(String clientId, String message) {
        StreamObserver<ChatVO> responseObserver = client_id_2_response_observer.get(clientId);
        if (ObjUtil.isNull(responseObserver)) {
            throw new RuntimeException("无法找到clientId为" + clientId + "的响应对象");
        }

        ChatVO vo = ChatVO.newBuilder().setMessage(message).build();
        responseObserver.onNext(vo);
    }

    @Override
    public void disconnectClient(String clientId) {
        StreamObserver<ChatVO> responseObserver = client_id_2_response_observer.remove(clientId);
        if (ObjUtil.isNull(responseObserver)) {
            throw new RuntimeException("无法找到clientId为" + clientId + "的响应对象");
        }

//        responseObserver.onError(new RuntimeException("服务端主动断开连接了"));
        responseObserver.onCompleted();
    }
}
