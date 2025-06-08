package io.github.nbb.grpc.client.grpc;

import com.hp.grpc.api.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.ServerInfo;
import org.apache.coyote.http11.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 胡鹏
 */
@Slf4j
public abstract class RpcClient {

    protected ScheduledExecutorService clientEventExecutor;

    private final BlockingQueue<ReconnectContext> reconnectionSignal = new ArrayBlockingQueue<>(1);

    protected RpcClientConfig rpcClientConfig = new RpcClientConfig();

    private long lastActiveTimeStamp = System.currentTimeMillis();

    protected volatile Connection currentConnection;

    /**
     * 处理程序来处理服务器推送请求。
     */
    protected List<ServerRequestHandler> serverRequestHandlers = new ArrayList<>();


    public final void start() {

        clientEventExecutor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r);
            t.setName("com.alibaba.nacos.client.remote.worker");
            t.setDaemon(true);
            return t;
        });


    }

    /**
     * 处理服务端发送的数据
     * @param request
     * @return
     */
    protected MessageDTO handleServerRequest(final MessageDTO request) {
        lastActiveTimeStamp = System.currentTimeMillis();
        for (ServerRequestHandler serverRequestHandler: serverRequestHandlers) {
            try {
                MessageDTO response = serverRequestHandler.requestReply(request, currentConnection);
                if (response != null) {
                    log.info("确认了服务端推送的信息");
                    return response;
                }
            } catch (Exception e) {
                log.error("处理服务端推送的信息出错了，错误信息为：{}", e.getMessage());
                throw e;
            }
        }
        return null;
    }

    /**
     * connect to server.
     *
     * @param serverInfo server address to connect.
     * @return return connection when successfully connect to server, or null if failed.
     * @throws Exception exception when fail to connect to server.
     */
    public abstract Connection connectToServer(ServerInfo serverInfo) throws Exception;




    class ReconnectContext {

        public ReconnectContext(ServerInfo serverInfo, boolean onRequestFail) {
            this.onRequestFail = onRequestFail;
            this.serverInfo = serverInfo;
        }

        boolean onRequestFail;

        ServerInfo serverInfo;
    }

    public static class ServerInfo {

        protected String serverIp;

        protected int serverPort;

        public ServerInfo() {

        }

        public ServerInfo(String serverIp, int serverPort) {
            this.serverPort = serverPort;
            this.serverIp = serverIp;
        }

        public String getAddress() {
            return serverIp + Constants.COLON + serverPort;
        }

        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }

        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        public String getServerIp() {
            return serverIp;
        }

        public int getServerPort() {
            return serverPort;
        }

        @Override
        public String toString() {
            return "{serverIp = '" + serverIp + '\'' + ", server main port = " + serverPort + '}';
        }
    }
}
