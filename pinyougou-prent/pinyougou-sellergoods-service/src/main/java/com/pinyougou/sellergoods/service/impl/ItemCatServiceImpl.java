package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired; 
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;  

import com.pinyougou.sellergoods.service.ItemCatService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat>  implements ItemCatService {

	
	private TbItemCatMapper itemCatMapper;

	@Autowired
	public ItemCatServiceImpl(TbItemCatMapper itemCatMapper) {
		super(itemCatMapper, TbItemCat.class);
		this.itemCatMapper=itemCatMapper;
	}

	
	

	
	@Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbItemCat> all = itemCatMapper.selectAll();
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat itemCat) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        if(itemCat!=null){			
						if(StringUtils.isNotBlank(itemCat.getName())){
				criteria.andLike("name","%"+itemCat.getName()+"%");
				//criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}
        List<TbItemCat> all = itemCatMapper.selectByExample(example);
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    /**
     * 新建、修改、删除之后都需要调用这个方法
     * 所以在这个方法中加redis缓存
     *1.加入redis的依赖
     * 2.配置redis的配置文件
     * 3.   注入redis模版调用方法
     *
     * 根据商品分类表的id（商品分类表的ID为ParentID的父类ID）输入parentID去查询它对应的子信息
     *
     * @param parentId
     * @return
     */

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        //1.创建TbItemCat（商品分类表）对象
        TbItemCat cat = new TbItemCat();
        //System.out.println("adsdDD d"+parentId);
        //2.将表中的一级类目ID设置进
        cat.setParentId(parentId);
        //3.根据这个一级类目ID进行查询所有的子ID信息
        List<TbItemCat> itemCats = itemCatMapper.select(cat);

        //将商品分类的数据存储到redis中
        //将查询到的分类数据存储到redis缓存中，获取分类列表并遍历
        List<TbItemCat> all = this.findAll();
        for (TbItemCat cat1 : all) {
            //将获取到的分类列表的数设置key为“itemCat”,值有，分类列表中的商品名称与模板ID
             redisTemplate.boundHashOps("itemCat").put(cat1.getName(),cat1.getTypeId());
        }



        return itemCats;
    }


}
