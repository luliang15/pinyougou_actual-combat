package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
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



@RequestMapping("/sepcifcation")
@RestController
public class SpecificationsController {

    //注入规格
    @Reference
    SpecificationService specificationService;
    @RequestMapping("/add")
    public Result add(String name) throws UnsupportedEncodingException {
        name = new String(name.getBytes("ISO8859-1"), "utf-8");
        try {
            TbSpecification tbSpecification = new TbSpecification();
            tbSpecification.setSpecName(name);
            tbSpecification.setSpecStatus("0");

            specificationService.add(tbSpecification);

            return new Result(true,"成功添加,需要审核");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"网络开小差了。。");

        }
    }


    @RequestMapping("/sepcfindAll")
    public List<TbSpecification> sepcfindAll(){
        List<TbSpecification> list = specificationService.selectAll();
        return list;

    }


    @RequestMapping("/findPage")
    public PageInfo<TbSpecification> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return specificationService.findPage(pageNo, pageSize);
    }



    @RequestMapping("/search")
    public PageInfo<TbSpecification> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbSpecification sepec) {


        return specificationService.findPage(pageNo, pageSize, sepec);
    }


    //模板申请页面加载查询审核过的规格进行回显
    @RequestMapping("/findAll")
    public List<TbSpecification> findAll(){
        TbSpecification tbSpecification = new TbSpecification();
        tbSpecification.setSpecStatus("1");
        return specificationService.select(tbSpecification);
    }


}
