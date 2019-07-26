package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 作者:传奇
 * 包名:com.pinyougou.shop.controller
 * 创建时间:2019/7/25 9:16
 */



@RequestMapping("/brand")
@RestController
public class BrandController {

    //注入品牌
    @Reference
    BrandService brandService;
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) throws UnsupportedEncodingException {
        try {
            brandService.add(brand);
            return new Result(true,"成功添加,需要审核");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"网络开小差了。。");

        }
    }


    @RequestMapping("/sepcfindAll")
    public List<TbBrand> sepcfindAll(){
        List<TbBrand> list = brandService.selectAll();
        return list;

    }


    @RequestMapping("/findPage")
    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return brandService.findPage(pageNo, pageSize);
    }



    @RequestMapping("/search")
    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbBrand tbBrand) {


        return brandService.findPage(pageNo, pageSize, tbBrand);
    }



    //模板页面加载查询查询所有审核过的品牌
    @RequestMapping("/findAll")
    public List<TbBrand> findPage() {
        TbBrand tbBrand = new TbBrand();
        tbBrand.setBrandStatus("1");
        List<TbBrand> tbBrands = brandService.select(tbBrand);
        return tbBrands;
    }


}
