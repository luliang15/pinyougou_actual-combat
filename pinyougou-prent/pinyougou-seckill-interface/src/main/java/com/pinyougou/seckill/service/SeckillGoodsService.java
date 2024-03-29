package com.pinyougou.seckill.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbSeckillGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService extends CoreService<TbSeckillGoods> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize);

	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize, TbSeckillGoods SeckillGoods);

    /**
     * 根据id获取到秒杀商品，设置秒杀商品的开始抢购与秒杀结束时间
     * @param id  秒杀商品id
     * @return
     */
    Map getGoodsById(Long id);


}
