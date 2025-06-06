package io.github.nbb.grpc.client.grpc;

import org.apache.coyote.http11.Constants;

import java.util.concurrent.*;

/**
 * @author 胡鹏
 */
public abstract class RpcClient {

    protected ScheduledExecutorService clientEventExecutor;

    private final BlockingQueue<ReconnectContext> reconnectionSignal = new ArrayBlockingQueue<>(1);

    protected RpcClientConfig rpcClientConfig = new RpcClientConfig();

    private long lastActiveTimeStamp = System.currentTimeMillis();


    public final void start() {

        clientEventExecutor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r);
            t.setName("com.alibaba.nacos.client.remote.worker");
            t.setDaemon(true);
            return t;
        });


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
