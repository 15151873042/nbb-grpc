package io.github.nbb.grpc.client.grpc;

/**
 * @author 胡鹏
 */
public abstract class Connection {

    private String connectionId;

    protected RpcClient.ServerInfo serverInfo;

    public String getConnectionId() {
        return connectionId;
    }

    public Connection(RpcClient.ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
