package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbItemCat;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService extends CoreService<TbItemCat> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat ItemCat);

    /**
     * 根据TbItemCat表的一级类目Id查询它的所有子ID
     * @param parentId
     * @return
     */

    List<TbItemCat> findByParentId(Long parentId);

    /**
     * 使用查询item商品分类的所有数据信息，使用redis存取
     * @return
     */
    List<TbItemCat> findShow();

    /**
     * //根据id查询出item商品列表的数据list
     * //list中再包含一个map，map中有多个键值对，第一个键值是二级类目的key与value
     * //第二个键值对中是三级类目的可以与value
     * @param parentId
     * @return
     */
    List<Map> findById(Long parentId);
}
