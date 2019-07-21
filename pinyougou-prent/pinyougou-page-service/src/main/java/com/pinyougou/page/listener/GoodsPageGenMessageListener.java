package com.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.rocketmq.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName:GoodsPageGenMessageListener
 * @Author：Mr.lee
 * @DATE：2019/07/08
 * @TIME： 9:14
 * @Description: TODO
 */
//获取消息体的类
public class GoodsPageGenMessageListener implements MessageListenerConcurrently {


    @Autowired  //注入消费的消息对象
    private ItemPageService itemPageService;


    /**
     * 消费获取消息的方法
     * @param list
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        try {
            //判断接收到的消息不为空
            if (list != null) {
                //1.遍历消息对象
                for (MessageExt msg : list) {
                    //2.获取消息体
                    byte[] body = msg.getBody();
                    //转字符串
                    String str = new String(body);

                    //把接收的字符串消息体转成mesageInfo对象
                    //3.获取消息体对应的自定义的消息对象
                    MessageInfo info = JSON.parseObject(str, MessageInfo.class);

                    //4.获取方法类型(用于判断)
                    int method = info.getMethod();

                    //5.判断方法是否为，做页面的生产、页面的修改、页面的删除
                    switch (method){
                        case 1:{
                            //1.页面的生成
                            //获取对应的字符串
                            updatePageHtml(info, "This is Add! 1");
                            break;
                        }
                        case 2:{    //2.页面的修改
                            updatePageHtml(info, "This is Update! 2");
                            break;
                        }
                        case 3:{
                            //3.页面的删除
                            //SPU id de 数组对应的字符串
                            String string = info.getContext().toString();
                            //字符串转成JSON对象
                            Long[] longs = JSON.parseObject(string, Long[].class);
                            //调用方法删除  删除静态页
                            itemPageService.deleteByIds(longs);
                            System.out.println("This is Delete! 3");
                            break;
                        }
                        default:{
                            //最后如果返回的消息都不正确。显示异常
                            throw new RuntimeException("Incorrect message sent!!!");
                        }
                    }

                }
            }
            //如果没有异常，则正常接收消息
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果是异常，则显示报错异常
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

    private void updatePageHtml(MessageInfo info, String s) {

        //获取到的是Map对象 并不能直接序列化回来 需要直接转成字符串
        String string = info.getContext().toString();
        //字符串转成对应的json对象
        List<TbItem> items = JSON.parseArray(string, TbItem.class);

        Set<Long> set = new HashSet<>();
        for (TbItem item : items) {
            //遍历生成的静态页面
            set.add(item.getGoodsId());//根据id生产的静态页面
        }
        //遍历set,生成静态页面
        for (Long aLong : set) {

            //调用方法  SPU的id
            itemPageService.genItemHtml(aLong);
        }

        System.out.println(s);
    }
}
