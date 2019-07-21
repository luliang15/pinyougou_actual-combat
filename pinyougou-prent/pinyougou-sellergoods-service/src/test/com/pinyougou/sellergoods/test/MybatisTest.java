package com.pinyougou.sellergoods.test;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @ClassName:MybatisTest
 * @Author：Mr.lee
 * @DATE：2019/06/22
 * @TIME： 19:54
 * @Description: TODO
 */
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisTest {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    //查询
    @Test
    public void test01(){

        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);

        for (TbBrand tbBrand : tbBrands) {
            System.out.println(tbBrand.getId()+":"+tbBrand.getName());
        }
    }

    //新增
    @Test
    public void test02(){

        TbBrand brand = new TbBrand();
        brand.setId(32L);
        brand.setName("Leess");
        brand.setFirstChar("L");
        tbBrandMapper.insert(brand);
    }

    //修改
    @Test
    public void test03(){

        TbBrand brand = new TbBrand();

        brand.setId(32L);
        brand.setName("HHHS");
        brand.setFirstChar("H");
        tbBrandMapper.updateByPrimaryKey(brand);

    }

    //删除
    @Test
    public void test04(){

        tbBrandMapper.deleteByPrimaryKey(32L);
    }
}
