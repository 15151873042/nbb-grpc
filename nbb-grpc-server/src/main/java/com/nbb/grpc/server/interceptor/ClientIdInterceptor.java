package com.nbb.grpc.server.interceptor;

import io.grpc.*;
import io.grpc.ServerCall.Listener;
import org.springframework.stereotype.Component;

/**
 * @author 胡鹏
 */
@Component
public class ClientIdInterceptor implements ServerInterceptor {

    public static final Context.Key<String> CLIENT_ID_CONTEXT_KEY = Context.key("client-id");
    private static final Metadata.Key<String> CLIENT_ID_KEY = Metadata.Key.of("client-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        // 从 headers 中提取 client-id
        String clientId = headers.get(CLIENT_ID_KEY);
        // 将 client-id 存入当前 Context
        Context context = Context.current().withValue(CLIENT_ID_CONTEXT_KEY, clientId);
        // 继续执行后续逻辑
        return Contexts.interceptCall(context, call, headers, next);
    }
}
