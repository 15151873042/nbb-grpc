package io.github.nbb.grpc.client.grpc;

import com.hp.grpc.api.MessageDTO;

public interface ServerRequestHandler {

    /**
     * 处理来自服务器的请求。
     *
     * @param request request
     * @param connection current connection, it can be used to know server ability
     * @return response.
     */
    MessageDTO requestReply(MessageDTO request, Connection connection);
}
