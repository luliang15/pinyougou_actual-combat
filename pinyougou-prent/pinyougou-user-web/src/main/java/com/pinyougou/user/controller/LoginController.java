package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName:LoginController
 * @Author：Mr.lee
 * @DATE：2019/07/10
 * @TIME： 21:29
 * @Description: TODO
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public String getName(){
        //根据安全框架获取到用户名称响应到前端页面
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
