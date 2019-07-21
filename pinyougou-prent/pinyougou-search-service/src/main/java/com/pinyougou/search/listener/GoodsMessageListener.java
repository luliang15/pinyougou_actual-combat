package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;

import com.pinyougou.common.rocketmq.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName:GoodsMessageListener
 * @Author：Mr.lee
 * @DATE：2019/07/07
 * @TIME： 21:29
 * @Description: TODO
 */

/**
 * 监听器的类
 */
public class GoodsMessageListener implements MessageListenerConcurrently{

    @Autowired  //注入searchService
    private ItemSearchService itemSearchService;

    /**
     * 重写获取消息的方法
     * @param list
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {

            if (list != null) {

                System.out.println("start:+++++++");
                //1.循环遍历消息对象
                for (MessageExt msg : list) {
                    //2.获取消息内容body
                    byte[] body = msg.getBody();
                    String str = new String(body);//获取到的消息字符串
                    MessageInfo info = JSON.parseObject(str,MessageInfo.class);

                    //3.获取方法（add、delete、update）
                    int method = info.getMethod();//1.新增、2.修改、3.删除
                    System.out.println("=========zhixingfangfa："+method);

                    //4.判断方法是 add、delete、update 分别进行对应的CRUD的操作
                    switch (method){

                        case 1:{  //如果是1，表示要新增
                            //获取商品列表的字符串
                            String context1 = info.getContext().toString();
                            //数据转JSON对象
                            List<TbItem> itemList = JSON.parseArray(context1, TbItem.class);
                            //调用修改的方法
                            itemSearchService.updateIndex(itemList);
                            System.out.println("This is Add! 1" );
                            break;
                        }
                        case 2: {//如果是2，表示要修改，更新
                            //获取商品列表的字符串
                            String context1 = info.getContext().toString();
                            //数据转JSON对象
                            List<TbItem> itemList = JSON.parseArray(context1, TbItem.class);
                            //调用修改的方法
                            itemSearchService.updateIndex(itemList);
                            System.out.println("This is Update! 2");
                            break;
                        }
                        case 3:{//如果是3，表示要删除
                            //获取SPU 的id的数组，也是字符串
                            String context1 = info.getContext().toString();
                            //这里因为ids中的id有可能重复，使用set集合去除重复id的操作
                            Set<Long> arr = new HashSet<>();


                            //也需要转成json对象
                            Long[] aLong = JSON.parseObject(context1, Long[].class);


                            //调用删除的方法
                            itemSearchService.deleteByIds(aLong);
                            System.out.println("This is Delete! 3");
                            break;
                        }
                        default:{
                            //表示最后
                            throw new RuntimeException("Incorrect message sent!!!");
                        }
                    }
                }
            }
            //如果没有异常，则正常获取消息体
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果出现异常，则提示等会儿再试
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
