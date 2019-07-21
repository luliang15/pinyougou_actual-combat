package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbSeller;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SellerService extends CoreService<TbSeller> {


    /**
     * 添加数据
     *
     * @param seller
     * @return
     */
    @Override
    void add(TbSeller seller);

    /**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeller> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeller> findPage(Integer pageNo, Integer pageSize, TbSeller Seller);

    /**
     * 修改审核商家状态是否成功的方法
     * @param sellerId
     * @param status
     */
    void updateStatus(String sellerId, String status);
}
