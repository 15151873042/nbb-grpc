package com.hp.grpc.consume.interceptor;

import io.grpc.*;
import org.springframework.stereotype.Component;

@Component
public class MyInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("custom-attribute", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                // 将自定义属性添加到请求头
                headers.put(CUSTOM_HEADER_KEY, "custom-value-from-client");
                super.start(responseListener, headers);
            }
        };
    }
}
