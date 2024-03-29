package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.utils.POIUtils;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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
    public Result delete(@RequestBody Long[] ids) {
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
            @RequestBody TbBrand tbBrand) {
        //根据条件进行模糊查询并分页

        PageInfo<TbBrand> pageInfo = brandService.findPage(pageNo, pageSize, tbBrand);

        return pageInfo;
    }


    //poi导入数据
    @RequestMapping("/upload")
    public Result uploadFile(@RequestParam MultipartFile file) throws Exception {

        try {
            String filename = file.getOriginalFilename();

            if (!filename.contains("xls")) {
                return new Result(false, "请上传正确的格式文件！！不要作死！");
            }
            if (filename.contains("xlsx")) {
                return new Result(false, "请上传正确的格式文件！！不要作死！");
            }
            List<String[]> rowList = POIUtils.readExcel(file);
            List<TbBrand> list = new ArrayList<>();
            for (int i = 0; i < rowList.size(); i++) {
                String[] row = rowList.get(i);
                TbBrand tbBrand = new TbBrand();
                tbBrand.setName(row[0]);
                tbBrand.setFirstChar(row[1]);
                //状态默认为0未审核
                tbBrand.setBrandStatus("0");
                list.add(tbBrand);
                //brandService.add(tbBrand);
            }
            String jsonString = JSON.toJSONString(list);
            return new Result(true, jsonString);
        } catch (Exception e) {

            e.printStackTrace();
            return new Result(false, "导入数据异常");
        }


    }


    //poi导入数据
    @RequestMapping("/into")
    public Result into(@RequestBody List<TbBrand> tbBrands) throws Exception {
        try {
            if (tbBrands != null) {
                for (TbBrand tbBrand : tbBrands) {

                    brandService.add(tbBrand);
                }
            }
            return new Result(true, "导入数据成功");
        } catch (Exception e) {

            e.printStackTrace();
            return new Result(false, "导入数据失败");
        }
    }


    //商品审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestBody Long[] ids, String status) {

        try {
            brandService.updateStatus(ids, status);
            return new Result(true, "审核成功。");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "网络有点差，请稍后再试");
        }

    }

}