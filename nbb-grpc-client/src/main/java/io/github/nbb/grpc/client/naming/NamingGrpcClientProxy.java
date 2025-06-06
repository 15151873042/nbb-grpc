package io.github.nbb.grpc.client.naming;

import io.github.nbb.grpc.client.grpc.GrpcSdkClient;
import io.github.nbb.grpc.client.grpc.RpcClient;

/**
 * @author 胡鹏
 */
public class NamingGrpcClientProxy {

    private final RpcClient rpcClient;


    public NamingGrpcClientProxy() {
        this.rpcClient = new GrpcSdkClient();

    }

    private void start() {
        rpcClient.start();
    }
}
