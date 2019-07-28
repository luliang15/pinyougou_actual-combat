package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;  

import com.pinyougou.sellergoods.service.TypeTemplateService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl extends CoreServiceImpl<TbTypeTemplate>  implements TypeTemplateService {

	
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired  //注入规格选项表的pojo对象
    private TbSpecificationOptionMapper optionMapper;

	@Autowired
	public TypeTemplateServiceImpl(TbTypeTemplateMapper typeTemplateMapper) {
		super(typeTemplateMapper, TbTypeTemplate.class);
		this.typeTemplateMapper=typeTemplateMapper;
	}

	
	

	
	@Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbTypeTemplate> all = typeTemplateMapper.selectAll();
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Autowired  //注入redis模板
    private RedisTemplate redisTemplate;

    /**
     * 分页查询方法
     * @param pageNo 当前页 码
     * @param pageSize 每页记录数
     * @param typeTemplate
     * @return
     */
	 @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if(typeTemplate!=null){			
						if(StringUtils.isNotBlank(typeTemplate.getName())){
				criteria.andLike("name","%"+typeTemplate.getName()+"%");
				//criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getSpecIds())){
				criteria.andLike("specIds","%"+typeTemplate.getSpecIds()+"%");
				//criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getBrandIds())){
				criteria.andLike("brandIds","%"+typeTemplate.getBrandIds()+"%");
				//criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getCustomAttributeItems())){
				criteria.andLike("customAttributeItems","%"+typeTemplate.getCustomAttributeItems()+"%");
				//criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
        List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);

         //将模板数据也存储到redis中，因为每次分类列表做完增删查改的情况下都要刷新页面

         //1.先获取模板的数据
         List<TbTypeTemplate> list = this.findAll();

         //2.循环遍历模板的数据
         for (TbTypeTemplate template : list) {
             //3.把数据存储到redis中
             String brandIds = template.getBrandIds();//在数据库中是jSOn字符串
             List<Map> mapList = JSON.parseArray(brandIds, Map.class);
             //设置模板数据的key  值为模板的id与模板的品牌名称列表   //
                redisTemplate.boundHashOps("brandList").put(template.getId(),mapList);

                //规格列表的存储
             //这个是根据模板id获取规格列表的数据。调用下面的findSpecList方法，且此方法也帮助将数据转成json对象了
             List<Map> specList = findSpecList(template.getId());

             //String specIds = template.getSpecIds(); //[{id:'1',"text":'网络',option[]}]
             redisTemplate.boundHashOps("specList").put(template.getId(),specList);
         }




        return pageInfo;
    }



    /**
     * 根据模块id查询出模板对象，再根据模板的spec_ids，查询出规格的数据
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> findSpecList(Long id) {

        //1.根据模板id查询并创建模板对象
        TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);

        //2.获取模板对象中规格列表的数据字符串
        String specIds = template.getSpecIds();

        //此时数据库中specIds中的数据为json字符串，需要先转成json对象
        //此时获取到整个specIds的值，但此时使用list<Map>封装着的
        List<Map> maps = JSON.parseArray(specIds, Map.class);

        //3.根据规格的id,获取规格选项的列表
        //遍历maps,获取根据键获取值的方式来获取到specIds中的值
        for (Map map : maps) {
            //默认会是Integer类型
            Integer specId = (Integer) map.get("id");

            //创建一个规格选项表的对象
            TbSpecificationOption option = new TbSpecificationOption();

            //设置id进行查询,此时option的id在数据库的类型是Long,这里需要转换
            option.setSpecId(Long.valueOf(specId));

            //根据规格的ID找到规格的选项列表信息
            //select * from tb_option where spec_Id=specId
            List<TbSpecificationOption> options = optionMapper.select(option);

            //此时查询出来的数据形式为:map ====>[{id:'1',"text":'网络',option[]}]
            //还需拼接option[]
            //4.拼接数据
            map.put("options",options);
            //此时添加的字段数据形式为：option[{id:1,optionName:"256G"}]
        }

      

        //5.返回list

        return maps;
    }
    //更新状态
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbTypeTemplate tbTypeTemplate = new TbTypeTemplate();
        tbTypeTemplate.setTemplateStatus(status);
            Example example = new Example(TbTypeTemplate.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", Arrays.asList(ids));
            if (ids != null) {
                typeTemplateMapper.updateByExampleSelective(tbTypeTemplate, example);
            }
        }

}
