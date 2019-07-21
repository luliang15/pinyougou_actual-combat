package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;


import entity.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName:BrandController
 * @Author：Mr.lee
 * @DATE：2019/06/23
 * @TIME： 9:47
 * @Description: TODO
 */
@RestController  //等于@Controller +  @ResponseBody ，json响应，下面的类都不用写 @ResponseBody
@RequestMapping("/brand")
public class BrandController {

    @Reference  //远程注入的注解，使用dubbo框架得来的
    private BrandService brandService;

    //查询TbBrand表的全部内容信息
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {

        //System.out.println("brandService"+brandService);

        List<TbBrand> all = brandService.findAll();
        // System.out.println("sgrse"+all);
        return all;

    }

    // 使用分页查询所有
    @RequestMapping("/findPage")
    public PageInfo<TbBrand> findPage(
            @RequestParam(value = "pageNo", required = true, defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize) {
        return brandService.findPage(pageNo, pageSize);
    }

    //添加列表品牌,前端传递的参数，需要加RequestBody注解
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand) {
        //添加是否成功
        try {
            brandService.add(tbBrand);
            //添加成功
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            //添加失败
            return new Result(false, "添加失败");
        }
    }

    //修改品牌列表的方法
    //先根据品牌列表的id查询出对应的TbBrand对象,使用resdul风格带参数传递
    @RequestMapping("/findOne/{id}")
    public TbBrand findOne(@PathVariable(name = "id") Long id) {

        return brandService.findOne(id);
    }

    //修改的方法，是否修改成功
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand) {

        try {
            brandService.update(tbBrand);
            //修改成功
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            //修改失败
            return new Result(false, "修改失败");
        }
    }

    //批量删除的方法,根据id删除对应的品牌对象
    //定义一个品牌数组的id参数，使之可以批量删除、
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        try {
            brandService.delete(ids);
            //修改成功
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            //修改失败
            return new Result(false, "删除失败");
        }
    }

    //根据条件模糊查询并分页显示
    //参数需要pageNo当前页，pageSize每页显示行数，还有要进行模糊查询的条件参数，直接传品牌对象
    @RequestMapping("/search")
    public PageInfo<TbBrand> search(
            @RequestParam(value = "pageNo", required = true, defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize,
            @RequestBody TbBrand tbBrand ){
        //根据条件进行模糊查询并分页

        PageInfo<TbBrand> pageInfo = brandService.findPage(pageNo, pageSize, tbBrand);

        return pageInfo;
    }
}