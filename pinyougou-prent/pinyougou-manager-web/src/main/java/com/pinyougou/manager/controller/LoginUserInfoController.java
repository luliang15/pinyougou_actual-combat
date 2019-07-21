package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName:LoginUserInfoController
 * @Author：Mr.lee
 * @DATE：2019/06/26
 * @TIME： 8:54
 * @Description: TODO
 */

@RestController  //表示controller +
public class LoginUserInfoController {

    @RequestMapping("/login/user/info")
    public String getUserInfo(){
        //获取登录用户的用户名
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
