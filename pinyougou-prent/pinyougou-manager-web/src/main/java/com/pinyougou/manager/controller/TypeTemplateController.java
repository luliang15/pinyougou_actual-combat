package com.pinyougou.manager.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.common.utils.POIUtils;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import com.github.pagehelper.PageInfo;
import entity.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

	@Reference
	private TypeTemplateService typeTemplateService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbTypeTemplate> findAll(){			
		return typeTemplateService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbTypeTemplate> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return typeTemplateService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param typeTemplate
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbTypeTemplate typeTemplate){
		try {
			typeTemplateService.add(typeTemplate);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param typeTemplate
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbTypeTemplate typeTemplate){
		try {
			typeTemplateService.update(typeTemplate);
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
	public TbTypeTemplate findOne(@PathVariable(value = "id") Long id){
		return typeTemplateService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			typeTemplateService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbTypeTemplate> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbTypeTemplate typeTemplate) {
        return typeTemplateService.findPage(pageNo, pageSize, typeTemplate);
    }

	@RequestMapping("/upload")
	public Result uploadFile(@RequestParam MultipartFile file) throws Exception {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			List<String[]> rowList = POIUtils.readExcel(file);
			for (int i = 0; i < rowList.size(); i++) {
				String[] row = rowList.get(i);
				TbTypeTemplate  tbSpecification = new TbTypeTemplate();
				tbSpecification.setName(row[0]);
				tbSpecification.setSpecIds(row[1]);
				tbSpecification.setBrandIds(row[2]);
				tbSpecification.setCustomAttributeItems(row[3]);
				typeTemplateService.add(tbSpecification);
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
			typeTemplateService.updateStatus(ids,status);
			return new Result(true, "审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "网络有点差，请稍后再试");
		}

	}
	
}
