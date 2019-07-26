package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.utils.ExcleImport;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.OrderList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController<T> {

	@Reference  //SPU
	private GoodsService goodsService;

  	@Reference
	private OrderService orderService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {

		List<TbOrder> orders = orderService.findAll();
        String[] headers={"订单ID","实付金额","支付类型","邮费","状态","订单创建时间",
				"订单更新时间","付款时间","发货时间","交易完成时间","交易关闭时间","物流名称","物流单号",
				"用户id","卖家留言","买家昵称","买家是否已经评价","收货地区","收货电话","收货人邮编","收货人",
				"过期时间，定期清理","发票类型","订单来源","商家id"};
		String realPath = session.getServletContext().getRealPath("/download/orderTable.xlsx");
        OutputStream  out = new FileOutputStream(realPath);

        try {
			ExcleImport. exportExcel("品优购",headers,orders,out,"yyy-MM-dd");


        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return orders;
	}
	@RequestMapping("/findAllOrder")
	public List<OrderList> findAllOrder(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		List<OrderList> orders= orderService.findAllOrder();
		return orders;

	}
}
