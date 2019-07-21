package com.pinyougou.seckill.thread;

/**
 * @ClassName:CreateOrderThread
 * @Author：Mr.lee
 * @DATE：2019/07/17
 * @TIME： 14:24
 * @Description: TODO
 */

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.rocketmq.MessageInfo;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

/**
 * 创建订单的线程处理
 * 多线程操作类
 *
 */
public class CreateOrderThread {

    @Autowired   //注入redis模板实例化bean
    private RedisTemplate redisTemplate;


    @Autowired  //注入秒杀商品实例化bean映射对象
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private DefaultMQProducer producer;


    //多线程的注解：表示异步  下面的方法异步执行
    @Async
    public void handleCreateOrder(){

        System.out.println("CreateOrder========>start()"+Thread.currentThread().getName());
        try {
            //模拟多线程操作
            //线程睡眠的10秒钟可操作 算积分、算优惠、存储用户的操作日志、大数据分析
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("CreateOrder========>end()"+Thread.currentThread().getName());

        //从redis队列中获取排队的元素（抢购用户的id、抢购的商品id）
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(
                SysConstants.SEC_KILL_USER_ORDER_LIST).rightPop();

        //判断SeckillStatus不为空
        if(seckillStatus != null){

            //获取用户id
            String userId = seckillStatus.getUserId();
            //获取商品id
            Long id = seckillStatus.getGoodsId();

            //判断能走到这里则证明商品库存还有
            TbSeckillGoods seckillGoods = (TbSeckillGoods)
                    redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(id);

            //3.减库存,抢到，就减一个库存
            seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
            //库存减少之后重新设置进redis中
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(id,seckillGoods);

            //4.判断 如果库存为0 更新到数据库中 删除redis中的商品
            if(seckillGoods.getStockCount() <= 0){ //如果被抢光
                //如果库存为0
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                //删除redis中的商品
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(id);
            }

            //5.创建 秒杀的预订单到redis中
            TbSeckillOrder seckillOrder = new TbSeckillOrder();

            seckillOrder.setId( new IdWorker(0,2).nextId());//设置订单的ID 这个就是out_trade_no
            seckillOrder.setCreateTime(new Date());//创建时间
            seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格  价格
            seckillOrder.setSeckillId(id);//秒杀商品的ID
            seckillOrder.setSellerId(seckillGoods.getSellerId());  //商家id
            seckillOrder.setUserId(userId);//设置用户ID
            seckillOrder.setStatus("0");//状态 未支付

            //将构建的订单保存到redis中  在秒杀订单中，userId就是唯一的标识
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(userId, seckillOrder);

            //当订单创建成功，移除掉用户储存的一个正在排队的标识
            redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());


            //发送消息（延时消息）
            MessageInfo messageInfo = new MessageInfo(
                    "TOPIC_SECKILL_DELAY",
                    "TAG_SECKILL_DELAY",
                    "handleOrder_DELAY",
                    seckillOrder,
                    MessageInfo.METHOD_UPDATE); //更新的业务，做超时的处理

            Message message = new Message(
                    messageInfo.getTopic(),
                    messageInfo.getTags(),
                    messageInfo.getKeys(),
                    JSON.toJSONString(messageInfo).getBytes());

            try {
                //设置延时的等级
                //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                //设置消息演示等级 16=30m
                message.setDelayTimeLevel(5); //使用第5级，延时一分钟
                //发送消息
                SendResult send = producer.send(message);

                System.out.println(send);

                System.out.println("fa song xiaoxi ==========");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
