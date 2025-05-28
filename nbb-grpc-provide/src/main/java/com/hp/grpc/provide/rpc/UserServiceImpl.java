package com.hp.grpc.provide.rpc;

import com.hp.grpc.api.UserDetailDTO;
import com.hp.grpc.api.UserDetailVO;
import com.hp.grpc.api.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * @author 胡鹏
 */
@Service
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUserDetail(UserDetailDTO dto, StreamObserver<UserDetailVO> responseObserver) {
        UserDetailVO vo = UserDetailVO.newBuilder()
                .setId(dto.getId())
                .setName("张三")
                .build();
        responseObserver.onNext(vo);
        responseObserver.onCompleted();
    }
}
