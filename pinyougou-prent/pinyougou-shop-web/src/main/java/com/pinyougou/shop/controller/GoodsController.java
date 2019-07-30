package com.pinyougou.shop.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.rocketmq.MessageInfo;
import com.pinyougou.pojo.TbItem;
import entity.Goods;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import com.github.pagehelper.PageInfo;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }

    /**
     * 添加商品，添加商品需要涉及到商品的名称（商品表）
     * 涉及到商品的描述
     * 涉及商品的SKu
     * 所以给入的参数需要是三个表的结合对象pojo  Goods
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //1.获取到商家的ID
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //2.商家ID在做商品添加时需要，将商家ID添加进goods中，goods是组合对象
            goods.getGoods().setSellerId(sellerId);

            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改  组合对象的修改，需改动成Goods
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     * 根据id获取整个组合对象的数据信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public Goods findOne(@PathVariable(value = "id") Long id) {
        //根据id查询获取整合组合对象的数据信息
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {

        //添加一个商家的条件，根据商家的名称查询显示
        goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());

        return goodsService.findPage(pageNo, pageSize, goods);
    }


    @Autowired
    private DefaultMQProducer producer;

    /**
     * 批量上架/下架
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/changeIsMarketable")
    public Result changeIsMarketable(@RequestBody Long[] ids, Integer status) {

        try {

            if (ids == null || ids.length <= 0) {
                throw new RuntimeException("请先勾选您需要上架的商品!");
            }

            goodsService.changeIsMarketable(ids, status);

            MessageInfo info;

            if ("0".equals(status)) {

                //设置删除的消息的主题、标签、keys、body(消息体、商品数据)
                info = new MessageInfo(
                        "Goods_Topic",  //消息主题
                        "goods_unmarketable_tag",//消息标签
                        "unmarketable", //消息的唯一标识
                        ids,   //body，消息体，根插此id查询到要删除的数据
                        MessageInfo.METHOD_DELETE);  //消息发送的类型，属于修改更新

            } else {


                //根据主键查询的商品数据信息
                List<TbItem> itemList = goodsService.findTbItemListByIds(ids);

                //设置更新的消息的主题、标签、keys、body(消息体、商品数据)
                info = new MessageInfo(
                        "Goods_Topic",  //消息主题
                        "goods_marketable_tag",//消息标签
                        "marketable", //消息的唯一标识
                        itemList,   //body，消息体，查询到的商品数据
                        MessageInfo.METHOD_UPDATE);  //消息发送的类型，属于修改更新
            }

            //转JSON
            String str = JSON.toJSONString(info);

            //获取消息体的主题、标签、keys(唯一标识)、body（主要消息体,就是商品数据）
            Message message = new Message(
                    info.getTopic(),
                    info.getTags(),
                    info.getKeys(),
                    str.getBytes()
            );

            SendResult send = producer.send(message);
            //将获取到要删除的消息进行打印
            System.out.println("Delete:" + send);

            return new Result(true, "成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }

    }

}
