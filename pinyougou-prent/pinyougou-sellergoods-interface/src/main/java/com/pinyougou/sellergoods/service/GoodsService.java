package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;
import entity.Goods;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {
	
	//商品
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);

    /**
     * 三个表结合的接口方法
     * @param goods
     */
    void add(Goods goods);

    /**
     * 自已定义的一个根据id获取查询整个组合对象的数据信息
     * @param id
     * @return
     */
    public Goods findOne(Long id);

    /**
     *
     * @param goods
     * @return
     */
     void update(Goods goods);

    /**
     * 自己定义的一个批量id去更新、修改商品状态的方法
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 根据SPU(主键的数组)的ids数据对象查询所有的该商品的列表SKU数据
     * @param ids
     * @return
     */
    List<TbItem> findTbItemListByIds(Long[] ids);
}
