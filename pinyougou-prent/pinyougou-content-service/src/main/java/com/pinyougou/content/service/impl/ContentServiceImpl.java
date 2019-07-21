package com.pinyougou.content.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;  





/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl extends CoreServiceImpl<TbContent>  implements ContentService {



	private TbContentMapper contentMapper;

	@Autowired
	public ContentServiceImpl(TbContentMapper contentMapper) {
		super(contentMapper, TbContent.class);
		this.contentMapper=contentMapper;
	}

	
	

	
	@Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbContent> all = contentMapper.selectAll();
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent content) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        if(content!=null){			
						if(StringUtils.isNotBlank(content.getTitle())){
				criteria.andLike("title","%"+content.getTitle()+"%");
				//criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(content.getUrl())){
				criteria.andLike("url","%"+content.getUrl()+"%");
				//criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(StringUtils.isNotBlank(content.getPic())){
				criteria.andLike("pic","%"+content.getPic()+"%");
				//criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(StringUtils.isNotBlank(content.getContent())){
				criteria.andLike("content","%"+content.getContent()+"%");
				//criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(StringUtils.isNotBlank(content.getStatus())){
				criteria.andLike("status","%"+content.getStatus()+"%");
				//criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
        List<TbContent> all = contentMapper.selectByExample(example);
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    @Autowired  //注入redis模版的Bean对象
    private RedisTemplate redisTemplate;

    /**首页的信息，首页的信息需要使用redis缓存起来，为数据库解压
     * 根据id查询对应的广告列表的信息
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<TbContent> findByCategoryId(Long categoryId) {

        //1.查询redis的数据。如果有，返回
        List<TbContent> content_redis = (List<TbContent>) redisTemplate.boundHashOps("CONTENT_REDIS").get(categoryId);

        //判断redis有数据的情况，有的话直接返回redis中的数据给前端
        if(content_redis!=null && content_redis.size()>0){

            System.out.println("ru guo you huan cun");
            return content_redis;
        }

        //否则没有，则进行以下的从数据库中查询数据的操作

        //2.如果没有。查询数据库的数据
        //根据广告分类的id获取广告列表 select * from tb_content where category_id=1
        TbContent tbContent = new TbContent();
        tbContent.setCategoryId(categoryId);

        List<TbContent> select = contentMapper.select(tbContent);


        //3.将查询到的数据库的数据 写入redis中

        redisTemplate.boundHashOps("CONTENT_REDIS").put(categoryId,select);

        System.out.println("mei you huan cun");

        return select;
    }

}
