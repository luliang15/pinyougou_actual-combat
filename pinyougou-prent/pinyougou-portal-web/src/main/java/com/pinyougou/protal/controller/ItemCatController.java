package com.pinyougou.protal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbItemCat;

import com.pinyougou.sellergoods.service.ItemCatService;
import entity.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * controller  商品分类表
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbItemCat> findAll(){			
		return itemCatService.findAll();
	}

    /**
     * 查询item的所有商品分类，如果第一次查询redis中没有数据，从数据库中查询
     * 之后将数据存进redis中，数据从redis中获取
     * @return
     */
	@RequestMapping("/findShow")
    public List<TbItemCat> findShow(){
	    return itemCatService.findShow();
    }
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbItemCat> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return itemCatService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.add(itemCat);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.update(itemCat);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbItemCat findOne(@PathVariable(value = "id") Long id){
		return itemCatService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			itemCatService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbItemCat> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbItemCat itemCat) {
        return itemCatService.findPage(pageNo, pageSize, itemCat);
    }

    //根据一级类目的ID查询显示一级类目下的所有子信息
    @RequestMapping("/findByParentId/{parentId}")
    public List<TbItemCat> findByParentId(@PathVariable(value = "parentId") Long parentId){

	    //1.根据商品分类表的id（商品分类表的ID为ParentID的父类ID）输入parentID去查询它对应的子信息
        //使用List集合来接收查询出来的返回值
        return itemCatService.findByParentId(parentId);

    }

    @RequestMapping("/findById/{parentId}")
    public List<Map> findById(@PathVariable(value = "parentId") Long parentId){

	    //根据id查询出item商品列表的数据list
        //list中再包含一个map，map中有多个键值对，第一个键值是二级类目的key与value
        //第二个键值对中是三级类目的可以与value

        List<Map> itemMaps = itemCatService.findById(parentId);

       

	    return itemMaps;
    }

}
