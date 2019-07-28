package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.utils.POIUtils;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.Result;
import entity.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

	@Reference
	private SpecificationService specificationService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSpecification> findAll(){			
		return specificationService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbSpecification> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return specificationService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加,新增
	 * @param specification
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Specification specification){
		try {
			specificationService.add(specification);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
     * 根据对应的规格表id修改规格表内容
     * 此时也可修改此id对应的规格表的规格选项内容(规格选项内容可能有多个)
	 * @param specification
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Specification specification){
		try {
			specificationService.update(specification);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 先根据id获取到规格表，再根据规格表中的规格选项id查询出对应的规格选项信息
     * 最后将规格表的内容信息与规格选项表的信息添加进中间表，返回中间表
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public Specification findOne(@PathVariable(value = "id") Long id){
		return specificationService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			specificationService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}



	@RequestMapping("/search")
    public PageInfo<TbSpecification> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbSpecification specification) {
        return specificationService.findPage(pageNo, pageSize, specification);
    }

	//文件上传的后台方法
	@RequestMapping("/upload")
	public Result uploadFile(@RequestParam MultipartFile file) throws Exception {
		try {
	/*		Map<String, Object> param = new HashMap<String, Object>();*/
			List<String[]> rowList = POIUtils.readExcel(file);
			for (int i = 0; i < rowList.size(); i++) {
				String[] row = rowList.get(i);
				TbSpecification specification = new TbSpecification();
				specification.setSpecName(row[0]);
				//默认为未审核
				specification.setSpecStatus("0");
				specificationService.add(specification);

			}
			return new Result(true, "导入数据成功");
		} catch (Exception e) {

			e.printStackTrace();
			return new Result(false, "导入数据有误！");
		}

	}

	//状态审核
	@RequestMapping("/updateStatus")
	public Result updateStatus(@RequestBody Long[] ids,String status){

		try {
			specificationService.updateStatus(ids,status);
			return new Result(true, "审核成功。。。");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "网络有点差，请稍后再试");
		}




	}
	
}
