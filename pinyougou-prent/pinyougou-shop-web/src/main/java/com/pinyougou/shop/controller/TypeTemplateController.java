package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
			//设置状态0 未审核
			typeTemplate.setTemplateStatus("0");
			typeTemplateService.add(typeTemplate);
			return new Result(true, "增加成功，进行审核");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "请查看网络。。。");
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
	 * 获取实体,根据模块ID查询对应模块的对象信息
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

    //根据模版ID查询模板对象，再根据模板对象查询出Spec_ids中规格的数据，再拼接
    @RequestMapping("/findSpecList/{id}")
    public List<Map> findSpecList(@PathVariable(name = "id") Long id){
	    //1.根据模板的ID  获取模板对象中规格的数据，拼接成格式：
        //[{id:1,'text':"网络",options:[{},{}]},{},]
        List<Map> mapList = typeTemplateService.findSpecList(id);

        return mapList;
    }
}
