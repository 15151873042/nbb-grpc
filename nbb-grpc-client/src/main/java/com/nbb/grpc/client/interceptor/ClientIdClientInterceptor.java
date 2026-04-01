package com.nbb.grpc.client.interceptor;

import io.grpc.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 胡鹏
 */
@Component
public class ClientIdClientInterceptor implements ClientInterceptor {

    @Value("${grpc-client.client-id}")
    private String clientId;

    private static final Metadata.Key<String> CLIENT_ID_KEY = Metadata.Key.of("client-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                // 将自定义属性添加到请求头
                headers.put(CLIENT_ID_KEY, clientId);
                super.start(responseListener, headers);
            }
        };
    }
}
