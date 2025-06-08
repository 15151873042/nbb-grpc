package io.github.nbb.grpc.client.grpc;

import com.hp.grpc.api.MessageDTO;
import com.hp.grpc.api.NormalServiceGrpc.NormalServiceFutureStub;
import com.hp.grpc.api.StreamServiceGrpc.StreamServiceFutureStub;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Executor;

public class GrpcConnection extends Connection {

    protected ManagedChannel channel;

    Executor executor;

    /** 发送普通请求的service */
    protected NormalServiceFutureStub normalServiceStub;

    protected StreamServiceFutureStub grpcFutureServiceStub;

    /** 发送流请求的Observer */
    protected StreamObserver<MessageDTO> payloadStreamObserver;

    public GrpcConnection(RpcClient.ServerInfo serverInfo, Executor executor) {
        super(serverInfo);
        this.executor = executor;
    }


    public void sendResponse(MessageDTO response) {
        payloadStreamObserver.onNext(response);
    }

    public void sendRequest(MessageDTO request) {
        payloadStreamObserver.onNext(request);
    }




    /**
     * Setter method for property <tt>payloadStreamObserver</tt>.
     *
     * @param payloadStreamObserver value to be assigned to property payloadStreamObserver
     */
    public void setPayloadStreamObserver(StreamObserver<MessageDTO> payloadStreamObserver) {
        this.payloadStreamObserver = payloadStreamObserver;
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    /**
     * Setter method for property <tt>grpcFutureServiceStub</tt>.
     *
     * @param grpcFutureServiceStub value to be assigned to property grpcFutureServiceStub
     */
    public void setGrpcFutureServiceStub(StreamServiceFutureStub grpcFutureServiceStub) {
        this.grpcFutureServiceStub = grpcFutureServiceStub;
    }
}
