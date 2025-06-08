package com.hp.grpc.consume.filter;

import io.grpc.Attributes;
import io.grpc.ClientTransportFilter;
import io.grpc.Context;
import io.grpc.Metadata;

import java.util.concurrent.atomic.AtomicReference;

public class CustomClientTransportFilter extends ClientTransportFilter {


    @Override
    public Attributes transportReady(Attributes transportAttrs) {
        transportAttrs.
    }

    @Override
    public void transportTerminated(Attributes transportAttrs) {
        super.transportTerminated(transportAttrs);
    }
}
