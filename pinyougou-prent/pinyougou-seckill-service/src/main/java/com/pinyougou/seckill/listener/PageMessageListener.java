package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.rocketmq.MessageInfo;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:PageMessageListener
 * @Author：Mr.lee
 * @DATE：2019/07/16
 * @TIME： 8:38
 * @Description: TODO
 */
public class PageMessageListener implements MessageListenerConcurrently {

    /**
     * 接收消息的消费方
     *
     * @param list
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        try {
            if(list!=null){

                for (MessageExt msg : list) {
                    //1.获取消息体
                    byte[] body = msg.getBody();
                    //2.字节转字符串
                    String str = new String(body);
                    //3.将字符串转成自定义的消息对象，有要的生成的商品的id的数组
                    MessageInfo info = JSON.parseObject(str,MessageInfo.class);

                    //4.判断 消息类型
                    if(info.getMethod() == MessageInfo.METHOD_ADD){

                        String s1 = info.getContext().toString();

                        //获取到秒杀id 的数组
                        Long[] longs = JSON.parseObject(s1, Long[].class);

                        //生成静态页码  定义一个方法，用于使用freemarker 根据秒杀商品的id生成静态页面
                        for (Long aLong : longs) {

                            genHTML("item.ftl",aLong);
                        }
                    }
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; //消息接收成功
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;//请等会儿再接收消息
        }
    }

    //注入生成静态页的模板对象
    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Value("${PageDir}")
    private String pageDir;

    /**
     *   生成静态页的方法
     * @param templateName  生成静态页的末班名称
     * @param id     秒杀商品的id
     */
    private void genHTML(String templateName,Long id){

        FileWriter writer =null;
        try {
            //1.创建一个configuration对象
            //2.设置字符编码 和 模板加载的目录
            Configuration configuration = configurer.getConfiguration();
            //3.获取模板对象
            Template template = configuration.getTemplate(templateName);
            //4.获取数据集
            Map model = new HashMap();

            //调用秒杀列表的消息方法，根据id获取秒杀商品
            TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
            model.put("seckillGoods",seckillGoods);

            //5.创建一个写流
            writer = new FileWriter(new File(pageDir+id+".html"));

            //6.调用模板对象的process 方法输出到指定的文件中
            template.process(model,writer);

        } catch (Exception e) {
            e.printStackTrace();

        }finally {

            //7.关闭流
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
