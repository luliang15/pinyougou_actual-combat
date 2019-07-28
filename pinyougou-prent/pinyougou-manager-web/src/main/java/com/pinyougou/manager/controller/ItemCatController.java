package com.pinyougou.manager.controller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.common.utils.ExcleImport;
import com.pinyougou.common.utils.POIUtils;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;

import com.github.pagehelper.PageInfo;
import entity.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
	public List<TbItemCat> findAll(HttpServletRequest request) throws FileNotFoundException {




		return itemCatService.findAll();
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



	@RequestMapping("/upload")
	public Result uploadFile(@RequestParam MultipartFile file) throws Exception {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			List<String[]> rowList = POIUtils.readExcel(file);
			for (int i = 0; i < rowList.size(); i++) {
				String[] row = rowList.get(i);
				TbItemCat itemCat = new TbItemCat();
				itemCat.setParentId(Long.valueOf(row[0]));
				itemCat.setName(row[1]);
				itemCat.setTypeId(Long.valueOf(row[2]));
				//梳妆台默认为0 未审核
				itemCat.setItemcatStatus("0");
				itemCatService.add(itemCat);
			}
			return new Result(true, "导入数据成功");
		} catch (Exception e) {

			e.printStackTrace();
			return new Result(false, "导入数据有误！");
		}


	}

	//审核状态更新
	@RequestMapping("/updateStatus")
	public Result updateStatus(@RequestBody Long[] ids,String status){

		try {
			itemCatService.updateStatus(ids,status);
			return new Result(true, "审核成功=");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "网络有点差，请稍后再试");
		}

	}

}
