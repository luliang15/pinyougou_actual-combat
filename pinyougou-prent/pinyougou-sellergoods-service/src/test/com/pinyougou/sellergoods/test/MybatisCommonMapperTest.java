package com.pinyougou.sellergoods.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:MybatisCommonMapperTest
 * @Author：Mr.lee
 * @DATE：2019/06/22
 * @TIME： 21:48
 * @Description: TODO
 */
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
@RunWith(SpringRunner.class)
public class MybatisCommonMapperTest {

    //使用spring-data做数据库的增删查改操作
    @Autowired
    private TbBrandMapper tbBrandMapper;

    //新增
    @Test
    public void insert(){
        TbBrand brand = new TbBrand();

        brand.setId(32L);
        brand.setName("黑马");
        brand.setFirstChar("k");

        tbBrandMapper.insert(brand);
    }

    //删除
    @Test
    public void delete(){
        //根据主键删除
        tbBrandMapper.deleteByPrimaryKey(47L);

        System.out.println("===============");

        //根据条件来删除  delete from tb_brand where id in (46)
        Example exmaple = new Example(TbBrand.class);
        Example.Criteria criteria = exmaple.createCriteria();
        List<Long> ids = new ArrayList<>();
        ids.add(46L);
        criteria.andIn("id",ids);
        tbBrandMapper.deleteByExample(exmaple);

        System.out.println("===============");

        //根据条件来删除 等号条件

        //delete from tb_brand where id=37
        TbBrand record = new TbBrand();
        record.setId(37L);
        tbBrandMapper.delete(record);

    }

    //查询
    @Test
    public void select(){

        TbBrand tbBrand = new TbBrand(); // 此时它表示条件
        tbBrand.setName("黑马");   //根据name属性为黑马去查询
        //select * from tb_bradn where name = "黑马"
        //List<TbBrand> tbBrands = tbBrandMapper.select(tbBrand);


        //条件指定的时候查询  tb_bradn的表  ，也作为条件来查询
        Example example = new Example(TbBrand.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name","黑马");  //where name = "黑马"
        criteria.andGreaterThan("id",40L);  //where id>40 的条件去查询
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(example);

        System.out.println(tbBrands);
    }

    //修改
    @Test
    public void update(){
        //update  tb_brand set id=? ,name=? ,firstchar=? where id=36
        //如果 firstchar 没有传递updateByPrimaryKey() 方法将会更新成null 到数据库中   updateByPrimaryKeySelective() 则不会更新null到数据库中
        TbBrand brandupdated = new TbBrand();
        brandupdated.setId(36L);
        brandupdated.setName("NNA");
        tbBrandMapper.updateByPrimaryKey(brandupdated);


        tbBrandMapper.updateByPrimaryKeySelective(brandupdated);
    }

    //测试自定义扩展查询
    @Test
    public void test08(){

        List<TbBrand> all = tbBrandMapper.findAll();

        for (TbBrand tbBrand : all) {
            System.out.println(tbBrand);
        }
    }

    //分页查询的测试
    @Test
    public void test09(){

        //page 当前页    size 每页显示多少条
        int page = 1;
        int size = 10;

        //分页处理,只需要调用PageHelper.startPage静态方法即可。S
        PageHelper.startPage(page,size);

        //调用查询的方法、
        List<TbBrand> all = tbBrandMapper.findAll();

        //获取分页信息,注意这里传入了brands集合对象
        PageInfo<TbBrand> pageInfo = new PageInfo<>(all);

        //打印输出分页效果
        System.out.println(pageInfo);

    }

}
