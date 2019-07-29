package com.pinyougou.manager.controller;
import java.util.List;
import java.util.Map;


import com.pinyougou.order.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Reference
	private SellerService sellerService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeller> findAll(){			
		return sellerService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbSeller> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return sellerService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param seller
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeller seller){
		try {
			sellerService.add(seller);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seller
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeller seller){
		try {
			sellerService.update(seller);
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
	public TbSeller findOne(@PathVariable(value = "id") String id){
		return sellerService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			sellerService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

    /**
     * 商家管理的审核查询
     * @param pageNo
     * @param pageSize
     * @param seller
     * @return
     */

	@RequestMapping("/search")
    public PageInfo<TbSeller> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbSeller seller) {
        return sellerService.findPage(pageNo, pageSize, seller);
    }

    /**
     * 修改深审核商家的是否合格状态
     * 给入两个参数，一个根据商家ID审核，还有审核的通过与否状态的参数
     * @param
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status) {

        try {
            sellerService.updateStatus(sellerId,status);
            //修改成功
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            //修改失败
            return new Result(false,"失败");
        }
    }

    @Reference
	private OrderService orderService;

	/**
	 * 所有的SPU的特定时间段的销售额
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping("/findSellInItem")
	public Map<String, Object> findSellInItem(String startTime, String endTime){


		String sellerId = null;

		Map<String, Object> map = orderService.findSellInItem(startTime,endTime,sellerId);

		return map;
	}

	/**
	 * 查询出所有的分类下的商品的销售额 比如 一级分类的销售额 二级分类的商品的销售额 三级分类的商品的销售额
	 * @return
	 */
	@RequestMapping("/findCategorySell")
	public Map<String,Object> findCategorySell(Long catNo1,Long catNo2,String startTime, String endTime){
		return orderService.findCategorySell(catNo1,catNo2,startTime,endTime);
	}

}
