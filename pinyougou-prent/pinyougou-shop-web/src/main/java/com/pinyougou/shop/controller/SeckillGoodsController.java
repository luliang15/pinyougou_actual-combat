package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import entity.Result;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillGoods> findAll(){			
		return seckillGoodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbSeckillGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return seckillGoodsService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillGoods seckillGoods,String startTime,String endTime){
		try {
			SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
			Date start = format.parse(startTime);
			Date end = format.parse(endTime);
			seckillGoods.setStartTime(start);
			seckillGoods.setEndTime(end);
			IdWorker idWorker=new IdWorker(0,0);
			long nextId = idWorker.nextId();
			seckillGoods.setGoodsId(nextId);

			seckillGoodsService.add(seckillGoods);

			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillGoods seckillGoods){
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
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbSeckillGoods findOne(@PathVariable(value = "id") Long id){
		return seckillGoodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			seckillGoodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/findPageGoods")
	public PageInfo<TbSeckillGoods> findPageGoods(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
												  @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
												  @RequestBody TbSeckillGoods tbSeckillGoods) {
		return seckillGoodsService.findPage(pageNo, pageSize,tbSeckillGoods);
	}

    /**
     * 根据商品id获取到秒杀商品，设置好秒杀商品的开始时间，与秒杀结束时间
     * @param id  商品id
     * @return
     */
    @RequestMapping("/getGoodsById")
    public Map getGoodsById(Long id){

        return seckillGoodsService.getGoodsById(id);
    }
}
