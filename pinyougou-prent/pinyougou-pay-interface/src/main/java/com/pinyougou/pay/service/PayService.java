package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @ClassName:PayService
 * @Author：Mr.lee
 * @DATE：2019/07/14
 * @TIME： 10:14
 * @Description: TODO
 */
public interface PayService {
    /**
     * 模拟浏览器发送请求 调用统一下单的APi 给微信支付系统，接收 响应 获取里面的 支付二维码的链接地址
     * @param out_trade_no  生成的订单号（此二维码的订单号）
     * @param total_fee  设置支付的金额
     * @return
     */
    Map<String, String> createNatice(String out_trade_no, String total_fee);

    /**
     * 检车某一个订单号的支付的状态，如果支付状态为success，则支付成功
     * @param out_trade_no
     * @return
     */
    Map<String, String> queryStatus(String out_trade_no);

    /**
     * 关闭微信订单（交易流水号）的方法
     * @param out_trade_no
     * @return
     */
    Map<String, String> closePay(String out_trade_no);

}
