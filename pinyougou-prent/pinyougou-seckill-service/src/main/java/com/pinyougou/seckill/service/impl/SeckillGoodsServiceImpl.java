package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;






/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl extends CoreServiceImpl<TbSeckillGoods>  implements SeckillGoodsService {

	
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	public SeckillGoodsServiceImpl(TbSeckillGoodsMapper seckillGoodsMapper) {
		super(seckillGoodsMapper, TbSeckillGoods.class);
		this.seckillGoodsMapper=seckillGoodsMapper;
	}

	
	

	
	@Override
    public PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSeckillGoods> all = seckillGoodsMapper.selectAll();
        PageInfo<TbSeckillGoods> info = new PageInfo<TbSeckillGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();

        if(seckillGoods!=null){			
						if(StringUtils.isNotBlank(seckillGoods.getTitle())){
				criteria.andLike("title","%"+seckillGoods.getTitle()+"%");
				//criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getSmallPic())){
				criteria.andLike("smallPic","%"+seckillGoods.getSmallPic()+"%");
				//criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getSellerId())){
				criteria.andLike("sellerId","%"+seckillGoods.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getStatus())){
				criteria.andLike("status","%"+seckillGoods.getStatus()+"%");
				//criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(seckillGoods.getIntroduction())){
				criteria.andLike("introduction","%"+seckillGoods.getIntroduction()+"%");
				//criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
        List<TbSeckillGoods> all = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> info = new PageInfo<TbSeckillGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    /**
     * 根据id获取到秒杀商品，设置秒杀商品的开始抢购与秒杀结束时间  以及库存
     *
     * @param id 秒杀商品id
     * @return
     */
    @Override
    public Map getGoodsById(Long id) {

        //1.从redis中获取到秒杀商品对象
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps
                (SysConstants.SEC_KILL_GOODS).get(id);

        //创建一个map
        Map map = new HashMap();

        //如果此秒杀商品不为null
        if(tbSeckillGoods != null){

            //设置剩余的毫秒数  与设置剩余的库存数
            map.put("time",tbSeckillGoods.getEndTime().getTime()-new Date().getTime());//结束时间减去当前时间就是剩余秒杀结束的时间
            map.put("count",tbSeckillGoods.getStockCount());  //设置库存

            return map;
        }

        return map;
    }


    @Autowired  //注入redis模板对象
    private RedisTemplate redisTemplate;

    /**
     * 重写秒杀表所有的数据信息
     * 但是不是从数据库中查询，现在是从redis中获取查询到的了
     * @return
     */
    @Override
    public List<TbSeckillGoods> findAll() {

        //values代表获取redis中所有的值
        List<TbSeckillGoods> seckillGoods = redisTemplate
                .boundHashOps(SysConstants.SEC_KILL_GOODS).values();

        return seckillGoods;
    }
}
