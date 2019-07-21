package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TbBrandMapper extends Mapper<TbBrand> {

    //继承了 Mapper<TbBrand>后会帮我们生成很多sql操作语句
    //但我们也可以自己扩展一些比较特殊的sql操作语句，在接口写接口方法
    //在对应的xml配置文件中写上对应的查询语句
    public List<TbBrand> findAll();
}