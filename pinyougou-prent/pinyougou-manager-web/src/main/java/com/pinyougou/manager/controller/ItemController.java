package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.utils.ExcleImport;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.ItemService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/item")
public class ItemController {

	@Reference
	private ItemService itemService;

	private static final String[] headers={"商品编号","商品标题","商品卖点","商品价格(单位:元)","stock_count","库存数量",
			"商品条形码","商品图片","分类的ID","商品状态","创建时间","更新时间","item_sn",
			"费用价格","市场价格","是否默认","商品id","商家","cart_thumbnail","category","brand",
			"规格","seller"};
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
	public List<TbItem> findAll(HttpSession session) throws FileNotFoundException {

		List<TbItem> all = itemService.findAll();
		String realPath = session.getServletContext().getRealPath("/download/itemAll.xlsx");
		OutputStream out = new FileOutputStream(realPath);

		try {
			ExcleImport. exportExcel("品优购",headers,all,out,"yyy-MM-dd");


		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}


		return all;
	}
	@RequestMapping("/findPage")
	public PageInfo<TbItem> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
									 @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
									 HttpSession session) throws FileNotFoundException {
		PageInfo<TbItem> page = itemService.findPage(pageNo, pageSize);
		String realPath = session.getServletContext().getRealPath("/download/item.xlsx");
		OutputStream out = new FileOutputStream(realPath);

		String s = JSON.toJSONString(page.getList());
		List<TbItem> list =JSON.parseArray(s, TbItem.class);
		try {
			ExcleImport. exportExcel("品优购",headers,list,out,"yyy-MM-dd");


		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return page;
	}


	@RequestMapping("/search")
	public PageInfo<TbItem> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                     @RequestBody TbItem item,HttpSession session) throws FileNotFoundException {
		PageInfo<TbItem> page = itemService.findPage(pageNo, pageSize, item);
		String realPath = session.getServletContext().getRealPath("/download/item.xlsx");
		OutputStream out = new FileOutputStream(realPath);

		String s = JSON.toJSONString(page.getList());
		List<TbItem> list =JSON.parseArray(s, TbItem.class);
		try {
			ExcleImport. exportExcel("品优购",headers,list,out,"yyy-MM-dd");


		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//添加一个商家的条件，根据商家的名称查询显示
		// goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());

		return page;
	}

	
	
	

}
