package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.rocketmq.MessageInfo;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;

import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference  //SPU
	private GoodsService goodsService;


	/*@Reference   //SKU  因为有了rocketmq,可移除依赖
    private ItemSearchService itemSearchService;*/

    /*@Reference  //注入生成静态页面的service
    private ItemPageService itemPageService;*/

    @Autowired //注入消息   生产者管理
    private DefaultMQProducer producer;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**添加商品，添加商品需要涉及到商品的名称（商品表）
     * 涉及到商品的描述
     * 涉及商品的SKu
     * 所以给入的参数需要是三个表的结合对象pojo  Goods
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
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
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
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
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
    public Goods findOne(@PathVariable(value = "id") Long id){
	    //根据id查询获取整合组合对象的数据信息
        return goodsService.findOne(id);
    }

    /*逻辑删除：逻辑删除的点击按钮，逻辑删除，数据不能根本地删除某个字段的值，只能修改某个字段的状态
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
		    //删除数据库
			goodsService.delete(ids);
			//获取被删除的SKU的数据

            //将这些SKU的数据从ES中移除掉，需要类似前面更新保存一般根据ids(多个id)进行删除
            //根据SPU的ID数组
            //itemSearchService.deleteByIds(ids);


            //设置删除的消息的主题、标签、keys、body(消息体、商品数据)
            MessageInfo info = new MessageInfo(
                    "Goods_Topic",  //消息主题
                    "goods_delete_tag",//消息标签
                    "deleteStatus", //消息的唯一标识
                    ids,   //body，消息体，根插此id查询到要删除的数据
                    MessageInfo.METHOD_DELETE);  //消息发送的类型，属于修改更新


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
            System.out.println("Delete:"+send);

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
       // goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());

        return goodsService.findPage(pageNo, pageSize, goods);
    }


    //需要创建一个新的修改方法，批量修改商品的状态，根据商品id

    /**
     *  //在这个方法中审核商品，更新状态的值，使用数据库与ES的数据同步
     * @param   ，使用数组接收
     * @param status  要修改的状态的参数
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestParam String status,@RequestBody Long[] ids){

        try {
            goodsService.updateStatus(ids,status);

            //做一个判断。只能是审核通过的时候才进行更新保存的操作
            if("1".equals(status)){
               /* //1.获取被审核的先通过SPU  获取到SPU的商品的数据
                List<TbItem> itemList = goodsService.findTbItemListByIds(ids);

                //2.将被审核的SKU的商品 的数据 更新到ES中

                //2.1引入search的服务

                //2.2调用更新索引的方法,将更新后的数据传入到ES的索引中更新,此方法内部再做保存更新数据到ES服务中的操作
                itemSearchService.updateIndex(itemList);

                //3 在安全状态审核通过的情况下，调用生成静态页面的方法
                //调用生成静态页面的方法，根据数据库对应要生成商品详情的id去生成
                //先遍历ids
                for (Long id : ids) {
                    //根据SKU的id去查询数据库中的数据，并且生成静态页面
                    itemPageService.genItemHtml(id);

                }*/


                   //现在不需要使用以上的方法去做更新删除或者添加的事了
                   //使用发送消息，将需要发送的消息全部封装到MessageInfo中
                   //根据主键查询的商品数据信息
                   List<TbItem> itemList = goodsService.findTbItemListByIds(ids);

                   //设置更新的消息的主题、标签、keys、body(消息体、商品数据)
                   MessageInfo info = new MessageInfo(
                           "Goods_Topic",  //消息主题
                           "goods_update_tag",//消息标签
                           "updateStatus", //消息的唯一标识
                           itemList,   //body，消息体，查询到的商品数据
                           MessageInfo.METHOD_UPDATE);  //消息发送的类型，属于修改更新


                   //因为info中的method方法都是字节，需要转成字符串
                   String str = JSON.toJSONString(info);

                   //获取消息体的主题、标签、keys(唯一标识)、body（主要消息体,就是商品数据）
                   Message message = new Message(
                           info.getTopic(),
                           info.getTags(),
                           info.getKeys(),
                           str.getBytes()
                   );

                   //发送信息
                   SendResult send = producer.send(message);

                   System.out.println("Update：" + send);

                }


            return new Result(true,"更新成功！！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败！！");
        }
    }
}
