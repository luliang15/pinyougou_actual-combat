package com.pinyougou.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName:PageController
 * @Author：Mr.lee
 * @DATE：2019/07/17
 * @TIME： 9:56
 * @Description: TODO
 */
@Controller()   //这个当登录跳板使用
@RequestMapping("/page")
public class PageController {

    /**
     *
     * @param url  携带的参数为原来的的秒杀抢购的路径
     * @return
     */
    @RequestMapping("/login")
    public String loginPage(String url){

        //重定向回到原来的的秒杀抢购的路径
        return  "redirect:"+url;
    }
}
