package com.pinyougou.manager.controller;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.rocketmq.MessageInfo;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;


import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

	@Reference
	private SeckillGoodsService seckillGoodsService;

	/**
	 * 返回全部列表
	 *
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsService.findAll();
	}


	@RequestMapping("/findPage")
	public PageInfo<TbSeckillGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
											 @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
		return seckillGoodsService.findPage(pageNo, pageSize);
	}

	/**
	 * 增加
	 *
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillGoods seckillGoods) {
		try {
			seckillGoodsService.add(seckillGoods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 *
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillGoods seckillGoods) {
		try {
			seckillGoodsService.update(seckillGoods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbSeckillGoods findOne(@PathVariable(value = "id") Long id) {
		return seckillGoodsService.findOne(id);
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
			seckillGoodsService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}


	@RequestMapping("/search")
	public PageInfo<TbSeckillGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
											 @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
											 @RequestBody TbSeckillGoods seckillGoods) {
		return seckillGoodsService.findPage(pageNo, pageSize, seckillGoods);
	}


	@Autowired  //注入发送消息的生产方对象
	private DefaultMQProducer producer;

	/**
	 * 批量审核秒杀商品的后台方法
	 *
	 * @param status
	 * @param ids
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(String status, @RequestBody Long[] ids) {

		try {
			//批量，所以遍历秒杀商品集合
			for (Long id : ids) {
				//创建秒杀商品列表对象，设置审核通过的条件
				TbSeckillGoods goods = new TbSeckillGoods();

				goods.setId(id);
				goods.setStatus(status);

				seckillGoodsService.updateByPrimaryKeySelective(goods);
			}


			//审核通过的情况下，发送消息，生成静态页面 ，使用自定义对象封装消息
			MessageInfo info = new MessageInfo(
					"TOPIC_SECKILL",          //主题
					"Tags_genHtml",          //tag 标签
					"seckillGoods_updateStatus",     //key 业务唯一标识
					ids,
					MessageInfo.METHOD_ADD    //消息类型：添加秒杀商品的静态页
			);

			//发送、
			SendResult send = producer.send(new Message(
					info.getTopic(),
					info.getTags(),
					info.getKeys(),
					JSON.toJSONString(info).getBytes()
			));

			//打印消息
			System.out.println(send);


			return new Result(true, "审核通过");

		} catch (Exception e) {
			e.printStackTrace();

			return new Result(false, "审核失败");
		}

	}

	/**
	 * 根据商品id获取到秒杀商品，设置好秒杀商品的开始时间，与秒杀结束时间
	 *
	 * @param id 商品id
	 * @return
	 */
	@RequestMapping("/getGoodsById")
	public Map getGoodsById(Long id) {

		return seckillGoodsService.getGoodsById(id);
	}

	@RequestMapping("/updateByStatus")
	public Result updateByStatus(String status,@RequestBody Long[] ids) {
		Result result = new Result(true,"123");
		try {
			//批量，所以遍历秒杀商品集合
			for (Long id : ids) {
				//创建秒杀商品列表对象，设置审核通过的条件
				TbSeckillGoods goods = new TbSeckillGoods();

				goods.setId(id);
				goods.setStatus(status);

				seckillGoodsService.updateByPrimaryKeySelective(goods);
			}
			return result = new Result(true,"审核通过");
		} catch (Exception e) {
			return result = new Result(false,"审核未通过");
		}

	}
}