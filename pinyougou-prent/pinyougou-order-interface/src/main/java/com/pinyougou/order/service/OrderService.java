package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import entity.OrderList;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:OrderService
 * @Author：Mr.lee
 * @DATE：2019/07/13
 * @TIME： 14:04
 * @Description: TODO
 */

//因为是订单提交，需要拆弹，  这里不继承通用Mapper
public interface OrderService {

    /**
     * 提交订单的方法
     * @param tbOrder
     */
    void add(TbOrder tbOrder);

    /**
     * 根据用户名从订单层获取存入redis中的记录支付日志对象
     * @param userId
     * @return
     */
    TbPayLog getPayLogFromRedis(String userId);

    /**
     * //1.更新支付日志的记录（交易的流水、交易的状态、交易的时间）
     * //2.更新日志支付的记录  关联订单的支付状态和支付时间
     * //3.删除该用户redis中的支付日志
     * @param transaction_id   微信订单号
     * @param out_trade_no    商户订单号
     */
    void updateOrderStatus(String transaction_id, String out_trade_no);

    Map<String, Object> findSellInOneTime(String startTime, String endTime, String sellerId);

    List<TbOrder> findAll();

    List<OrderList> findAllOrder();
}
