package com.hp.grpc.provide.interceptor;

import io.grpc.*;
import io.grpc.ServerCall.Listener;
import org.springframework.stereotype.Component;

/**
 * @author 胡鹏
 */
@Component
public class GrpcConnectionInterceptor implements ServerInterceptor {
    @Override
    public <T, S> Listener<T> interceptCall(ServerCall<T, S> call, Metadata headers, ServerCallHandler<T, S> next) {
        Context ctx = Context.current();

        return Contexts.interceptCall(ctx, call, headers, next);
    }
}
