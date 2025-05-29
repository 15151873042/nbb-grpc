package com.hp.grpc.consume.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hp.grpc.api.UserDetailDTO;
import com.hp.grpc.api.UserDetailVO;
import com.hp.grpc.api.UserServiceGrpc.UserServiceBlockingStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 胡鹏
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceBlockingStub userService;

    @RequestMapping("/")
    public String getUserDetail() {
        UserDetailDTO dto = UserDetailDTO.newBuilder().setId("1").build();

        UserDetailVO vo = userService.getUserDetail(dto);

        HashMap<String, String> result = new HashMap<>();
        result.put("id", vo.getId());
        result.put("name", vo.getName());


        return JSONUtil.toJsonStr(result);
    }

}
