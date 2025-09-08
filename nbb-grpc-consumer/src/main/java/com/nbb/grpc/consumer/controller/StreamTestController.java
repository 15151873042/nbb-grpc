package com.nbb.grpc.consumer.controller;

import com.nbb.grpc.consumer.service.StreamClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author 胡鹏
 */
@RestController
public class StreamTestController {

    @Autowired
    private StreamClientService streamClient;

    /**
     * 测试服务端流
     * 访问示例：http://localhost:8081/test-server-stream?content=hello&count=3
     */
    @GetMapping("/test-server-stream")
    public String testServerStream(@RequestParam String content,
                                   @RequestParam(defaultValue = "3") int count) throws InterruptedException {
        streamClient.callServerStream(content, count);
        return "服务端流测试完成，请查看日志";
    }

    /**
     * 测试客户端流
     * 访问示例：http://localhost:8081/test-client-stream?msgs=msg1,msg2,msg3
     */
    @GetMapping("/test-client-stream")
    public String testClientStream(@RequestParam String msgs) throws InterruptedException {
        List<String> messages = Arrays.asList(msgs.split(","));
        streamClient.callClientStream(messages);
        return "客户端流测试完成，请查看日志";
    }

    /**
     * 测试双向流
     * 访问示例：http://localhost:8081/test-bidirectional-stream?msgs=hi,how are you,bye
     */
    @GetMapping("/test-bidirectional-stream")
    public String testBidirectionalStream(@RequestParam String msgs) throws InterruptedException {
        List<String> messages = Arrays.asList(msgs.split(","));
        streamClient.callBidirectionalStream(messages);
        return "双向流测试完成，请查看日志";
    }
}
