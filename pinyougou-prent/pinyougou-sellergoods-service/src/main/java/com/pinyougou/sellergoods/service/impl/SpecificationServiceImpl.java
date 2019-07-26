package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.Specification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl extends CoreServiceImpl<TbSpecification>  implements SpecificationService {

	//规格表
	private TbSpecificationMapper specificationMapper;

	@Autowired   //规格的选项表
    private TbSpecificationOptionMapper optionMapper;

	@Autowired
	public SpecificationServiceImpl(TbSpecificationMapper specificationMapper) {
		super(specificationMapper, TbSpecification.class);
		this.specificationMapper=specificationMapper;
	}


    /**
     * 、
     * 根据id查询出对应的规格表信息与规格选项表信息后
     * 对修改之后的信息进行保存
     *
     * @param specification
     */
    @Override
    public void update(Specification specification) {

        //1.根据规格id修改对应id的内容信息
        specificationMapper.updateByPrimaryKey(specification.getSpecification());
        //2.创建规格选项表的对象
        TbSpecificationOption option= new TbSpecificationOption();
        //3.规格选项表添加进规格表的id，作为条件显示对应得规格选项内容
        option.setSpecId(specification.getSpecification().getId());
        //删除规格选项(作为修改得一项功能)
        int delete = optionMapper.delete(option);

        //根据中间表获取多个规格选项表的内容
        List<TbSpecificationOption> optionList = specification.getOptionList();
        //遍历规格选项
        for (TbSpecificationOption tbSpecificationOption : optionList) {
            //中间表中获取到规格表信息，将规格表的id作为条件传入规格选项表中
            //也就是修改这个id的规格表时，可同时对个规格表对应的规格选项进行修改
            tbSpecificationOption.setSpecId(specification.getSpecification().getId());
            //将修改之后的规格选项信息添加进规格选项表中
            optionMapper.insert(tbSpecificationOption);
        }
        //批量插入 要求 主键为ID 并且是自增才可以
        //optionMapper.insertList(optionList);
    }

    /**
     * 根据id回显组合pojo的数据信息，规格表与规格选项表的结合
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //1.创建规格表组合pojo对象    规格表与规格选项表的中间表
        Specification specification = new Specification();
        //2.根据id查询出对应的规格表
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

        //3.创建规格选项表对象
        TbSpecificationOption option = new TbSpecificationOption();
        //4.将规格选项表中的规格id作为条件查询传入
        option.setSpecId(tbSpecification.getId());
        //进行条件查询，一个规格可能有多个规格选项
        List<TbSpecificationOption> options = optionMapper.select(option);

        //5.将根据id查询出的规格表添加进规格选项表与规格表的中间表中
        specification.setSpecification(tbSpecification);

        //6.将根据规格表中的规格选项id也添加进中间表中
        specification.setOptionList(options);
        //返回结合两个表查询出来的中间表
        return specification;

    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Override
    public void delete(Long[] ids) {
        //删除规格
        Example example = new Example(TbSpecification.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        specificationMapper.deleteByExample(example);

        //删除规格关联的规格的选项
        Example exampleOption = new Example(TbSpecificationOption.class);
        exampleOption.createCriteria().andIn("specId", Arrays.asList(ids));
        optionMapper.deleteByExample(exampleOption);
    }

    /**
     * 新添加进来的方法
     * 添加数据
     *新增规格以及规格的规格选项
     * @param record
     * @return
     */
    @Override
    public void add(Specification record) {

        //1.获取规格的数据
        TbSpecification specification = record.getSpecification();
        //2.获取规格的选项的列表数据
        List<TbSpecificationOption> optionList = record.getOptionList();
        //3.将数据插入到两个表中

        //如果使用通用mapper，添加了主键 注解，@id  自动获取主键的值，放入pojo中的ID属性中
        specificationMapper.insert(specification);

        for (TbSpecificationOption tbSpecificationOption : optionList) {

            //设置一个规格表的Id，规格的id会与规格选项表相关联
            tbSpecificationOption.setSpecId(specification.getId());//设置规格的id
            //规格的规格选项添加方法
            optionMapper.insert(tbSpecificationOption);
        }

    }

    @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSpecification> all = specificationMapper.selectAll();
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();

        if(specification!=null){			
						if(StringUtils.isNotBlank(specification.getSpecName())){
				criteria.andLike("specName","%"+specification.getSpecName()+"%");
				//criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
        List<TbSpecification> all = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
