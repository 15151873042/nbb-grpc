package com.nbb.grpc.consumer.controller;

import com.nbb.grpc.consumer.service.HelloClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 胡鹏
 */
@RestController
public class HelloController {

    @Resource
    HelloClientService helloClientService;

    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return helloClientService.hello(name);
    }

    @GetMapping("/hello-again")
    public String helloAgain(@RequestParam String name) {
        return helloClientService.helloAgain(name);
    }
}
