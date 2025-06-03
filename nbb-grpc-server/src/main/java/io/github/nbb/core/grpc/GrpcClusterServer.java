package io.github.nbb.core.grpc;

import org.springframework.stereotype.Service;

/**
 * @author 胡鹏
 */
@Service
public class GrpcClusterServer extends BaseGrpcServer{
    @Override
    public int rpcPortOffset() {
        return 1001;
    }
}
