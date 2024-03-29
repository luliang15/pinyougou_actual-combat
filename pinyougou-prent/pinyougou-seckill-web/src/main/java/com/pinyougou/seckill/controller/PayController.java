package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.pay.service.PayService;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:PayController
 * @Author：Mr.lee
 * @DATE：2019/07/14
 * @TIME： 10:05
 * @Description: TODO
 */
@RestController
@RequestMapping("/pay")
public class PayController {


    @Reference  //注入微信支付服务对象
    private PayService payService;

    @Reference  //注入订单对象
    private SeckillOrderService seckillOrderService;

    /**
     * 使用品优购生成后台二维码的方法
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative(){

        //2.从redis中获取当前的用户所对应的秒杀订单（订单号、金额）
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder order = seckillOrderService.getUserOrderStatus(userId);

        //判断如果订单不为null
        if(order!=null){

            double v = order.getMoney().doubleValue();
            long totalMoney = (long)(v*100);     //需要转成分，微信支付中的单位是以分为单位

            //3.调用统一下单的API(调用服务的方法)
            Map<String,String> map = payService.createNatice(order.getId()+"",
                    totalMoney+"");

            //4.返回 订单号 有金额 有二维码链接
            return map;
        }

       return new HashMap();
    }

    /**
     * 此时生成了二维码支付页面，现在需要支付
     * 在扫码支付的时候，后台是需要不断地向微信支付服务发请求的，直到支付的状态为success时
     * 结束发送请求，但是，此时用户也可能在生成了二维码之后一直不支付，请求不能这样一直请求下去
     * 需要设置定时接收请求，如果在规范的时间内未收到支付服务的成功状态，则返回支付超时
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){

        Result result = new Result(false,"Payment loser");

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            //定义一个变量，统计超时的时间
             int count = 0;
            //1.调用支付的服务  不停的查询  状态
            //死循环，判断 在支付状态为成功时，一直向微信支付服务发送请求
            while (true){
               Map<String,String> resultMap =  payService.queryStatus(out_trade_no);

               count++;//开始计时，第一次计时

                //但是用户如果一直不支付，请求一直发送也不行，设置超时时间，如果超时5分钟就退出
                if(count>=100){

                    result =  new Result(false,"Payment overTime");

                    //如果超时还没有支付的情况
                    //1.关闭微信的订单号
                    Map<String, String> closeMap = payService.closePay(out_trade_no);

                    //如果为空
                    if(closeMap==null){
                        System.out.println("没有该订单");

                    }else if("ORDERPAID".equals(closeMap.get("err_code"))){//表示此用户可能在最后一刻支付了
                        seckillOrderService.updateOrderStatus("transaction_id",userId);

                    }else if("SUCCESS".equals(closeMap.get("result_code")) || "ORDERCLOSED".equals(closeMap.get("err_code"))){
                            //关闭订单成功

                        seckillOrderService.deleteOrder(userId);

                    }else {
                        //先不管，报错了要重新关闭这个订单
                    }


                    //结束循环
                    break;
                }

                //此时如果一直快速地发送请求，会造成内存溢出，需要让线程睡一会儿，再请求,3秒
                Thread.sleep(3000);


               //判断支付状态是否成功success,这些字段都是需要对照微信支付的API的
                if("SUCCESS".equals(resultMap.get("trade_state"))){
                    //此时为支付成功，结束循环
                    result = new Result(true,"Payment SUCCESS");

                    //创建一个新的方法来修改支付日志的记录 交易的流水、交易的状态、交易的时间）

                   // String transaction_id = resultMap.get("transaction_id");//获取到的微信订单号(交易流水号)

                    //如果秒杀订单已支付，修改秒杀订单的为已支付状态，将redis中的订单存储到mysql中
                    //且删除redis中的订单
                    seckillOrderService.updateOrderStatus("transaction_id",userId);

                    break;
                }
            }

            //2.返回结果
            return result;

        } catch (InterruptedException e) {
            e.printStackTrace();
            //支付失败
            return new Result(false,"Payment loser");
        }
    }
}
