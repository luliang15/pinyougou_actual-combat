package com.pinyougou.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName:BrandServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/06/23
 * @TIME： 9:57
 * @Description: TODO
 *
 * 继承抽象父类的实现类，简单的sql语句操作不用注解实现，继承父类的实现就行
 *给定指定要实现的pojo类型TbBrand
 */
@Service //anlbaba的注解
public class BrandServiceImpl extends CoreServiceImpl<TbBrand> implements BrandService {

    //注入dao层的Mapper
    //@Autowired  这里可以不用写Autowired的注解了，就是不用注入了，
    //因为BrandServiceImpl继承了核心服务实现类（父类）给了构造方法，并注入tbBrandMapper
    private TbBrandMapper tbBrandMapper;

    //给构造参数
    @Autowired
    public BrandServiceImpl(TbBrandMapper tbBrandMapper) {
        super(tbBrandMapper, TbBrand.class);
        this.tbBrandMapper = tbBrandMapper;
    }

    //使用分页查询所有
    @Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize) {

        //1.创建分页对象、pageNo表示第一页，pageSize表示每页显示条数
        PageHelper.startPage(pageNo,pageSize);
        //2.查询
        List<TbBrand> list = tbBrandMapper.findAll();
        //3.创建PageInfo对象
        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);

        //4.因为现在响应给客户端的是json对象，需要序列化PageInfo对象
        //先通过JSON序列化成一个字符串
        String str = JSON.toJSONString(pageInfo);
        //5.再将字符串（成对象）反序列化给前端
        PageInfo pageinfo1 = JSON.parseObject(str, PageInfo.class);

        return pageinfo1;
    }



    //根据条件进行模糊查询并分页显示
    @Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize, TbBrand tbBrand) {

     //1.获取条件对象
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();

        //2.组装条件，判断组装条件
        if(tbBrand!=null){
            //如果输入的品牌名称参数为空或者空字符串
            if(StringUtils.isNotBlank(tbBrand.getName())){
                criteria.andLike("name","%"+tbBrand.getName()+"%");//name like "联想"
            }
            //如果输入的首字母参数为空或者空字符串
            if(StringUtils.isNoneBlank(tbBrand.getFirstChar())){
                criteria.andLike("firstChar","%"+tbBrand.getFirstChar()+"%");//firstChar like "L"
            }
        }
        //3.执行查询
        PageHelper.startPage(pageNo,pageSize);
        // 4.开始分页,g根据条件查询
        List<TbBrand> list = tbBrandMapper.selectByExample(example);
        //5.返回PageInfo

        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);

        //.因为现在响应给客户端的是json对象，需要序列化PageInfo对象
        //先通过JSON序列化成一个字符串
        String str = JSON.toJSONString(pageInfo);
        //.再将字符串（成对象）反序列化给前端
        PageInfo pageinfo1 = JSON.parseObject(str, PageInfo.class);

        return pageinfo1;
    }


    //更新状态
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbBrand tbBrand = new TbBrand();
        tbBrand.setBrandStatus(status);
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        if (ids != null) {
            tbBrandMapper.updateByExampleSelective(tbBrand, example);
        }
    }


}

/*

    //根据品牌id查询对象的对象
    @Override
    public TbBrand findOne(Long id) {
        //根据主键查询对应的对象数据
        return tbBrandMapper.selectByPrimaryKey(id);
    }


    //批量删除的方法
    @Override
    public void delete(Long[] ids) {
        //第一种批量删除的sql语句方式,这种方式进行批量删除的话比较损耗内存
      */
/*  for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }*//*

        //第二种批量删除的方法，使用sq语句带条件的方式
        //delete from tb_brand where id in (1,2,3)
        Example example = new Example(TbBrand.class);//创建一个删除条件对象,要删除的是这个表
        //创建一个条件
        Example.Criteria criteria = example.createCriteria();

        //第一个参数指定pojo的参数，第二个参数指定对应的值，
        // 注意 Arrays.asList(ids)，这里是将ids数组参数转成集合参数传入方法中
        criteria.andIn("id", Arrays.asList(ids));
        //相当于delete from tb_brand example,example就表示一个条件
        tbBrandMapper.deleteByExample(example);

    }*/
