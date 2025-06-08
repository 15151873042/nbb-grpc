package io.github.nbb.grpc.client.grpc;

import cn.hutool.core.util.StrUtil;
import com.hp.grpc.api.MessageDTO;
import com.hp.grpc.api.NormalServiceGrpc;
import com.hp.grpc.api.StreamServiceGrpc;
import com.hp.grpc.api.StreamServiceGrpc.StreamServiceStub;
import io.github.nbb.grpc.client.utils.ThreadFactoryBuilder;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
@Slf4j
public abstract class GrpcClient extends RpcClient{

    /** 服务端告诉客户端连接已建立 */
    private final static String SETUP_ACK_REQUEST = "SetupAckRequest";
    /** 客户端告诉服务端连接已建立 */
    private final static String SETUP_ACK_RESPONSE = "SetupAckResponse";
    /** 客户端告诉服务端连接已建立 */
    private final static String CONNECTION_SETUP_REQUEST = "ConnectionSetupRequest";

    private ThreadPoolExecutor grpcExecutor;

    /** 处理服务端发送的消息 */
    private SetupRequestHandler setupRequestHandler = new SetupRequestHandler();


    public GrpcClient() {

    }

    private void sendResponse(MessageDTO response) {
        try {
            ((GrpcConnection) this.currentConnection).sendResponse(response);
        } catch (Exception e) {
            log.error("发送ack响应失败，ackId->{}", response.getRequestId());
        }
    }

    @Override
    public Connection connectToServer(ServerInfo serverInfo) throws Exception {
        try {
            if (grpcExecutor == null) {
                this.grpcExecutor = createGrpcExecutor(serverInfo.getServerIp());
            }
            int port = serverInfo.getServerPort() + rpcPortOffset();
            ManagedChannel managedChannel = createNewManagedChannel(serverInfo.getServerIp(), port);

            StreamServiceStub streamService = StreamServiceGrpc.newStub(managedChannel);

            GrpcConnection grpcConn = new GrpcConnection(serverInfo, grpcExecutor);
            grpcConn.setConnectionId("client-01");

            // 创建流请求，并将请求绑定道connection
            StreamObserver<MessageDTO> payloadStreamObserver = bindRequestStream(streamService, grpcConn);

            grpcConn.setPayloadStreamObserver(payloadStreamObserver);
            grpcConn.setChannel(managedChannel);
            grpcConn.sendRequest(MessageDTO.newBuilder().setMessage(CONNECTION_SETUP_REQUEST).build());

            Thread.sleep(100L);
            return grpcConn;
        } catch (Exception e) {
            log.error("grpc连接服务端失败，错误信息为：{}", e);
        }

        return null;
    }

    private StreamObserver<MessageDTO> bindRequestStream(final StreamServiceStub streamService, final  GrpcConnection grpcConn) {
        return streamService.send(new StreamObserver<MessageDTO>() {
            @Override
            public void onNext(MessageDTO messageDTO) {
                String serverResponseMessage = messageDTO.getMessage();
                log.info("[{}接收到service端StreamService的消息，消息内容为：{}]", grpcConn.getConnectionId(), serverResponseMessage);

                if (StrUtil.isNotEmpty(serverResponseMessage)) {
                    if ("SetupAckRequest".equals(serverResponseMessage)) {
                        setupRequestHandler.requestReply(messageDTO, null);
                        return;
                    }

                    // 处理服务端发送的消息
                    MessageDTO response = handleServerRequest(messageDTO);
                    if (response != null) {
                        sendResponse(response);
                    } else {
                        log.warn("处理服务端消息失败，ackId->", messageDTO.getRequestId());
                    }

                }


            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }


    protected NormalServiceGrpc.NormalServiceFutureStub createNewChannelStub(ManagedChannel managedChannelTemp) {
        return NormalServiceGrpc.newFutureStub(managedChannelTemp);
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

    /**
     * 设置响应处理程序。
     */
    class SetupRequestHandler implements ServerRequestHandler {

        @Override
        public MessageDTO requestReply(MessageDTO request, Connection connection) {
            if (SETUP_ACK_REQUEST.equals(request.getMessage())) {
                // TODO remove and count down
                return MessageDTO.newBuilder().setMessage(SETUP_ACK_RESPONSE).build();
            }
            return null;
        }
    }
}
