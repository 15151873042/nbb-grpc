package io.github.nbb.grpc.client.grpc;

import java.util.Map;

/**
 * @author 胡鹏
 */
public class GrpcSdkClient extends GrpcClient {

    public GrpcSdkClient() {
    }

    @Override
    public int rpcPortOffset() {
        return 1000;
    }
}
