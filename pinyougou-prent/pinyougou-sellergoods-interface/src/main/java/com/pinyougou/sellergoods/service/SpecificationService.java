package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbSpecification;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.Specification;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService extends CoreService<TbSpecification> {

    /**、
     * 根据id查询出对应的规格表信息与规格选项表信息后
     * 对修改之后的信息进行保存
     * @param specification
     */
    public void update(Specification specification);

    /**
     * 根据id回显组合pojo的数据信息，规格表与规格选项表的结合
     * @param id
     * @return
     */
    public Specification findOne(Long id);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    void delete(Long[] ids);

    /**新添加进来的方法
     * 添加数据
     *新增规格以及规格的规格选项
     * @param record
     * @return
     */

    void add(Specification record);


    /**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification Specification);
	
}
