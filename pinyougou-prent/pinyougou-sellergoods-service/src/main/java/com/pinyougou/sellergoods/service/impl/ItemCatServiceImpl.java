package com.pinyougou.sellergoods.service.impl;

import java.util.*;

import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.pojo.TbBrand;
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
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat> implements ItemCatService {

	
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
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (StringUtils.isNotBlank(itemCat.getName())) {
                criteria.andLike("name", "%" + itemCat.getName() + "%");
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

        //3.根据这个一级类目ID进行查询所有的子ID信息  一级id获取到的list
        List<TbItemCat> itemCats = itemCatMapper.select(cat);

        //将商品分类的数据存储到redis中
        //将查询到的分类数据存储到redis缓存中，获取分类列表并遍历
        List<TbItemCat> all = this.findAll();

        for (TbItemCat cat1 : all) {
            //将获取到的分类列表的数设置key为“itemCat”,值有，分类列表中的商品名称与模板ID  二级
             redisTemplate.boundHashOps("itemCat").put(cat1.getName(),cat1.getTypeId());
        }



        return itemCats;
    }

    /**
     * 使用查询item商品分类的所有数据信息，使用redis存取
     *
     * @return
     */
    @Override
    public List<TbItemCat> findShow() {

        List<TbItemCat> tbItemCats = null;

        //1.判断，如果第一次查询，redis中没有商品分类的数据的话，从数据库中查询
        if (redisTemplate.boundHashOps(SysConstants.ITEMCATLIST).get("itemCatAll") == null) {
            //2.查询到所有的item商品分类列表的数据
            tbItemCats = itemCatMapper.selectAll();
            System.out.println("cong shujuku cha!");
        }else {
            //现在从redis中获取到了数据赋值给商品分类表
           tbItemCats = (List<TbItemCat>) redisTemplate.boundHashOps(SysConstants.ITEMCATLIST).get("itemCatAll");
        }

        //3.将商品分类列表的数据存进redis中
        redisTemplate.boundHashOps(SysConstants.ITEMCATLIST).put("itemCatAll", tbItemCats);
        System.out.println("cong redis zhong cha!");

        return tbItemCats;
    }

    /**
     * //根据id查询出item商品列表的数据list
     * //list中再包含一个map，map中有多个键值对，第一个键值是二级类目的key与value
     * //第二个键值对中是三级类目的可以与value
     *
     * @param parentId 一级类目的id  就是二级类目的parentId
     * @return
     */
    @Override
    public List<Map> findById(Long parentId) {

        //1.创建一个listMap的容器
        List<Map> mapList = new ArrayList<>();

        TbItemCat itemCat = new TbItemCat();

        //根据传递的parentId查询到二级类目的数据
        itemCat.setParentId(parentId);

        //2.判断，当第一次从redis中没有获取到item商品列表的数据时   加上一级的id去做大key
        if(redisTemplate.boundHashOps(SysConstants.ITEMCATLIST).get("itemCatAll2"+parentId) == null){

            //从数据库中查询获取数据,二级类目的数据
            List<TbItemCat> tbItemCats2 = itemCatMapper.select(itemCat);

            //再根据二级类目的parentId获取到三级类目的数据
            for (TbItemCat tbItemCat : tbItemCats2) {

                HashMap<String, Object> map = new HashMap<>();

                //创建根据二级类目的id作为条件。二级的id就是三级的parentId
                itemCat.setParentId(tbItemCat.getId());

                //查询出三级类目的数据
                List<TbItemCat> tbItemCats3 = itemCatMapper.select(itemCat);

                //存进二级类目
                map.put("tbItemCats2",tbItemCat);

                //将三级类目的数据存进map中
                map.put("tbItemCats3",tbItemCats3);

                //最后将获取到二级与三级类目数据的map存进listMap中
                mapList.add(map);
            }

        }else {
            //否则则redis中有数据，不从数据库中查找，从redis中查询数据
            mapList = (List<Map>) redisTemplate.boundHashOps(SysConstants.ITEMCATLIST).get("itemCatAll2"+parentId);
        }


        //将封装好数据存进redis中
        redisTemplate.boundHashOps(SysConstants.ITEMCATLIST).put("itemCatAll2"+parentId,mapList);

        //最后返回封装好数据的mapList集合
        return mapList;
    }

    //更新状态cao
    @Override
    public void updateStatus(Long[] ids, String status) {

        TbItemCat itemCat = new TbItemCat();
        itemCat.setItemcatStatus(status);
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        if (ids!= null) {
            itemCatMapper.updateByExampleSelective(itemCat, example);
        }
    }
}
