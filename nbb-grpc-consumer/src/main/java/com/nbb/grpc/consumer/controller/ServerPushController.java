package com.nbb.grpc.consumer.controller;

import com.nbb.grpc.api.hello.HelloDTO;
import com.nbb.grpc.api.hello.HelloServiceGrpc;
import com.nbb.grpc.consumer.service.ServerPushClientService;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 胡鹏
 */
@RestController
public class ServerPushController {

    @Resource
    ServerPushClientService serverPushClientService;

    @GetMapping("/connect-server")
    public String connectServer() {
        serverPushClientService.startReceivingPush();
        return "success";
    }
}
