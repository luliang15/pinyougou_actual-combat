package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * @ClassName:BrandService
 * @Author：Mr.lee
 * @DATE：2019/06/23
 * @TIME： 9:51
 * @Description: TODO
 *
 * 继承核心服务型接口,继承之后给定指定的pojo对象泛型，使之可以实现，
 * 简单的sql语句操作接口方法都不用写了，继承父类的就行
 */
public interface BrandService extends CoreService<TbBrand> {


    //使用分页查询所有
    PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize);


    //根据条件进行吗，模糊查询并分页
    PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize, TbBrand tbBrand);

    void updateStatus(Long[] ids, String status);
}

   /* //添加列表的方法
    void add(TbBrand tbBrand);

    //根据品牌列表的id查询出品牌列表的对象
    TbBrand findOne(Long id);

    //修改的方法
    void update(TbBrand tbBrand);

    //批量删除的方法
    void delete(Long[] ids);


    //查询TbBrand表的所有内容
    List<TbBrand> findAll();
    */