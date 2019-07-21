package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.Result;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName:OrderController
 * @Author：Mr.lee
 * @DATE：2019/07/13
 * @TIME： 13:55
 * @Description: TODO
 */

/**在购物车中，选中好要买的商品
 * 提交订单的方法（订单需要拆单，因为有多个订单，且多个订单可能在不同个商家下买的商品）
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    //远程注入orderService，提交订单使用
    @Reference
    private OrderService orderService;

    /**
     * 提交订单的方法
     * 传入订单参数
     * @param tbOrder  订单对象
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbOrder tbOrder){

        try {
            //获取一个用户名称添加给提交订单中
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            tbOrder.setUserId(name);
            orderService.add(tbOrder);
            //提交订单成
            return new Result(true,"success!!");
        } catch (Exception e) {
            e.printStackTrace();
            //提交订单失败
            return new Result(false,"fairly!!");
        }

    }
}
