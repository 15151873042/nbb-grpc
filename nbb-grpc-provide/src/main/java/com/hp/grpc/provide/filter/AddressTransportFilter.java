package com.hp.grpc.provide.filter;

import io.grpc.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class AddressTransportFilter extends ServerTransportFilter {

    public static final Attributes.Key<String> CUSTOM_HEADER_KEY =
            Attributes.Key.create("custom-header");

    @Override
    public Attributes transportReady(Attributes transportAttrs) {
        // 获取与当前传输关联的Context
        Context context = Context.current();

        // 在新版本中，元数据通常通过拦截器或其他机制传递
        // 这里我们创建一个自定义的Context key来存储元数据
        AtomicReference<Metadata> metadataRef = new AtomicReference<>();

        // 将metadataRef存储到Attributes中，以便后续拦截器可以访问
        Attributes newAttrs = transportAttrs.withValue(
                Attributes.Key.<AtomicReference<Metadata>>create("metadata-ref"),
                metadataRef
        );

        // 虽然不能直接获取元数据，但我们可以设置回调或标记
        // 实际的元数据提取将在拦截器中完成

        return newAttrs;
    }

    @Override
    public void transportTerminated(Attributes transportAttrs) {
        super.transportTerminated(transportAttrs);
    }
}
