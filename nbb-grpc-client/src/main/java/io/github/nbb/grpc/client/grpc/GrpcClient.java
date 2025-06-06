package io.github.nbb.grpc.client.grpc;

import io.github.nbb.grpc.client.utils.ThreadFactoryBuilder;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
public abstract class GrpcClient extends RpcClient{

    private ThreadPoolExecutor grpcExecutor;


    public GrpcClient() {
    }

    @Override
    public Connection connectToServer(ServerInfo serverInfo) throws Exception {
        if (grpcExecutor == null) {
            this.grpcExecutor = createGrpcExecutor(serverInfo.getServerIp());
        }
        int port = serverInfo.getServerPort() + rpcPortOffset();
        ManagedChannel managedChannel = createNewManagedChannel(serverInfo.getServerIp(), port);

        return null;
    }

    private ManagedChannel createNewManagedChannel(String serverIp, int serverPort) {
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(serverIp, serverPort)
                .executor(grpcExecutor).compressorRegistry(CompressorRegistry.getDefaultInstance())
                .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
                .maxInboundMessageSize(rpcClientConfig.getMaxInboundMessageSize())
                .keepAliveTime(rpcClientConfig.getChannelKeepAlive(), TimeUnit.MILLISECONDS).usePlaintext();
        return managedChannelBuilder.build();
    }

    protected ThreadPoolExecutor createGrpcExecutor(String serverIp) {
        // Thread name will use String.format, ipv6 maybe contain special word %, so handle it first.
        serverIp = serverIp.replaceAll("%", "-");
        ThreadPoolExecutor grpcExecutor = new ThreadPoolExecutor(rpcClientConfig.getThreadPoolCoreSize(),
                rpcClientConfig.getThreadPoolMaxSize(), rpcClientConfig.getThreadPoolKeepAlive(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(rpcClientConfig.getThreadPoolQueueSize()),
                new ThreadFactoryBuilder().daemon(true).nameFormat("nacos-grpc-client-executor-" + serverIp + "-%d")
                        .build());
        grpcExecutor.allowCoreThreadTimeOut(true);
        return grpcExecutor;
    }

    public abstract int rpcPortOffset();
}
