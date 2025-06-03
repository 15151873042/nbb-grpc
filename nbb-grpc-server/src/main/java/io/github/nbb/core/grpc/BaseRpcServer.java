package io.github.nbb.core.grpc;

import io.github.nbb.core.env.EnvUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * @author 胡鹏
 */
@Slf4j
public abstract class BaseRpcServer {

    @PostConstruct
    public void start() {
        String serverName = getClass().getSimpleName();
        log.info("{} Rpc server starting at port {}", serverName, getServicePort());

        startServer();
    }

    public int getServicePort() {
        return EnvUtil.getPort() + rpcPortOffset();
    }

    /**
     * 启动Rpc Server
     * @throws Exception
     */
    public abstract void startServer();

    /**
     * RPC服务器端口偏移量。
     * @return 主端口的增量端口偏移量。
     */
    public abstract int rpcPortOffset();
}
