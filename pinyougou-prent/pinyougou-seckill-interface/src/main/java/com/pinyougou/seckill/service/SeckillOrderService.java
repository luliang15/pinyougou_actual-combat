package com.pinyougou.seckill.service;
import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService extends CoreService<TbSeckillOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder SeckillOrder);

    /**
     *  此方法用于创建用户抢到秒杀订单且将订单存进redis中的（预订单）
     * @param id   秒杀列表的商品id
     * @param userId   抢到商品的用户id
     */
    void submitOrder(Long id, String userId);

    /**
     * 查询订单状态，是否支付
     * @param userId
     * @return
     */
    TbSeckillOrder getUserOrderStatus(String userId);

    /**如果此用户的抢购秒杀订单已支付
     * 1.从redis中获取秒杀订单
     * 2.修改此订单的支付状态为已支付
     * 3.将此订单从redis中储存到mysql中
     * 4.redis中删除此订单
     *
     * @param transaction_id   交易流水号
     * @param userId          用户id
     */
    void updateOrderStatus(String transaction_id, String userId);

    /**
     *  //2.删除redis中的该用户对应对应的为支付的订单
     *  //3.恢复库存
     *  //4.恢复队列的元素
     * @param userId
     */
    void deleteOrder(String userId);
}
