package com.pinyougou.seckill.controller;
import java.util.List;
import java.util.Map;

import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;


import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

	@Reference
	private SeckillOrderService seckillOrderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillOrder> findAll(){			
		return seckillOrderService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbSeckillOrder> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return seckillOrderService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 秒杀商品的抢购 下单  抢到了秒杀的商品，此时要来下单了
     * 此时抢到的订单是存储在redis中的一个{预订单} ，提交到数据库且改变支付成功状态的才是真正的购买订单
	 * @param id  秒杀商品的id
	 * @return
	 */
	@RequestMapping("/submitOrder/{id}")
	public Result submitOrder(@PathVariable("id") Long id){
		try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            if("anonymousUser".equals(userId)){
                //403表示要登录
                return new Result(false,"403");
            }

            seckillOrderService.submitOrder(id,userId);

			return new Result(true, "正在排队中，请稍等...");

		} catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		}catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "订单创建失败");
        }
	}
	
	/**
	 * 修改
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.update(seckillOrder);
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
	public TbSeckillOrder findOne(@PathVariable(value = "id") Long id){
		return seckillOrderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			seckillOrderService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public Map<String, Object> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
										@RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
										@RequestBody TbSeckillOrder seckillOrder) {
        return seckillOrderService.findPage(pageNo, pageSize, seckillOrder);
    }

    /**
     *  查询订单状态，判断订单是否为null
     *  如果为null,则订单创建成功，待支付
     *  不为null，则正在排队创建订单中
     * @return
     */
    @RequestMapping("/queryOrderStatus")
    public Result queryOrderStatus(){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(userId)) {
                //表示没有登录
                return new Result(false, "403");
            }
            //判断查询订单状态
            TbSeckillOrder order = seckillOrderService.getUserOrderStatus(userId);
            //说明订单生成成功
            if(order !=null){
                return new Result(true, "订单创建成功，待支付");
            }else{
                return new Result(false, "正在排队中,请稍等...");
            }
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "抢单失败");
        }
    }
	
}
