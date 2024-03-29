package com.pinyougou.shop.controller;
import java.util.List;
import java.util.Map;


import com.pinyougou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired   //注入密码加密器
    private PasswordEncoder passwordEncoder;
	/**
	 * 增加
	 * @param seller
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeller seller){
		try {
		    //现在需要对刚注册的新用户的密码进行加密,对密码设置加密
            String encode = passwordEncoder.encode(seller.getPassword());
            //再将加密好的密码设置进将要注册的对象中
            seller.setPassword(encode);

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
	public TbSeller findOne(@PathVariable(value = "id") Long id){
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
	
	

	@RequestMapping("/search")
    public PageInfo<TbSeller> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbSeller seller) {
        return sellerService.findPage(pageNo, pageSize, seller);
    }


    @Reference
    private OrderService orderService;
	/**
	 * 根据日期查询 此日期间隔的每一天的总销售额
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping("/findSellInOneTime")
	public Map<String,Object> findSellInOneTime(String startTime,String endTime){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String,Object> map = orderService.findSellInOneTime(startTime,endTime,sellerId);

        return map;
    }



	/**
	 * 获取当前用户的所有的SPU的特定时间段的销售额
	 * @param startTime
	 * @param endTime
	 * @return
	 */
    @RequestMapping("/findSellInItem")
	public Map<String, Object> findSellInItem(String startTime, String endTime){
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

		Map<String, Object> map = orderService.findSellInItem(startTime,endTime,sellerId);
		return map;
	}

}
