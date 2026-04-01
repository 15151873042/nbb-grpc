package com.nbb.grpc.server.controller;

import com.nbb.grpc.server.service.ChatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author 胡鹏
 */
@RestController
public class ChatServerController {

    @Resource
    private ChatService chatService;


    /**
     * 查询所有客户端id
     * @return
     */
    @RequestMapping("/chat/list-all-client-id")
    public Set<String> listAllClientId() {
        return chatService.listAllClientId();
    }

    /**
     * 和指定客户端断开连接
     * @param clientId 客户端id
     * @return
     */
    @RequestMapping("/chat/disconnect-client")
    public String disconnectClient(String clientId) {
        chatService.disconnectClient(clientId);
        return "success";
    }


    /**
     * 向指定客户端推送消息
     * @param clientId 客户端id
     * @param message 消息内容
     * @return
     */
    @RequestMapping("/chat/push-message-to-client")
    public String pushMessageToClient(String clientId, String message) {
        chatService.pushMessageToClient(clientId, message);
        return "success";
    }
}
