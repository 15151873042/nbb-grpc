package com.nbb.grpc.server.service;

import java.util.Set;

/**
 * @author 胡鹏
 */
public interface ChatService {

    Set<String> listAllClientId();

    void pushMessageToClient(String clientId, String message);

    void disconnectClient(String clientId);
}
