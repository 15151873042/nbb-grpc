package io.github.nbb.core.grpc;

import io.github.nbb.core.env.EnvUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 胡鹏
 */
@Slf4j
public abstract class BaseRpcServer {

    @PostConstruct
    public void start() {
        String serverName = getClass().getSimpleName();
        log.info("{} Rpc服务启动中，占用端口 {}", serverName, getServicePort());

        startServer();

        log.info("{}  Rpc服务已启动，占用端口 {}", serverName, getServicePort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("{} Rpc服务正在停止。。。", serverName);
            try {
                BaseRpcServer.this.shutdownServer();
                log.info("{} Rpc服务成功停止了。。。", serverName);
            } catch (Exception e) {
                log.error("{} Rpc服务停止失败。。。", serverName);
            }

        }));
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


    public final void stopServer() {
        shutdownServer();
    }

    @PreDestroy
    public abstract void shutdownServer();
}
