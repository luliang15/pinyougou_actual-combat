package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.rocketmq.MessageInfo;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @ClassName:DelayMessageListener
 * @Author：Mr.lee
 * @DATE：2019/07/17
 * @TIME： 21:31
 * @Description: TODO
 */
public class DelayMessageListener implements MessageListenerConcurrently {


    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 接收消息方法
     * @param msgs
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    System.out.println("xiaoxi  ==========");
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);

                    //订单更新 标识即可
                    if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE) {


                        //获取Redis中的未支付订单的信息
                        TbSeckillOrder tbSeckillOrder = JSON.parseObject(messageInfo.getContext().toString(), TbSeckillOrder.class);

                        //获取数据库中的订单的信息
                        TbSeckillOrder seckillOrder = seckillOrderMapper.selectByPrimaryKey(tbSeckillOrder.getId());
                        if (seckillOrder == null) {
                            //关闭微信订单  如果 关闭微信订单的时候出现该订单已经关闭 则说明不需要 再恢复库存

                            /**
                             * if(微信订单的状态为没有关闭 或者 其他的错误){
                             *     调用删除订单 恢复库存的方法，
                             * }else{
                             *     啥也不干
                             * }
                             */
                            //删除订单
                            seckillOrderService.deleteOrder(tbSeckillOrder.getUserId());
                        }
                        //有订单 无需关心
                    }

                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
