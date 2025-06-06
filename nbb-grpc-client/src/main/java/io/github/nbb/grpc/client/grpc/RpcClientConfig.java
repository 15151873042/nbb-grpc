package io.github.nbb.grpc.client.grpc;

import lombok.Data;

/**
 * @author 胡鹏
 */
@Data
public class RpcClientConfig {

    /**
     * get connection keep alive time.
     */
    private long connectionKeepAlive = 5000;


    private int threadPoolCoreSize = 16;

    private int threadPoolMaxSize = 64;

    private int threadPoolKeepAlive = 10000;

    private int threadPoolQueueSize = 10000;

    private int maxInboundMessageSize = 10485760;

    private int channelKeepAlive = 360000;

}
