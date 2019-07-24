package com.pinyougou.seckill.task;

import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @ClassName:Task
 * @Author：Mr.lee
 * @DATE：2019/07/15
 * @TIME： 11:33
 * @Description: TODO
 */

@Component
public class Task {


    @Autowired  //注入秒杀表的映射对象
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired  //注入redis模板对象
    private RedisTemplate redisTemplate;

    /**
     * 定时 把数据库的数据 推送到redis中
     * cron:表达式用于指定何时去执行该方法
     * 从0秒钟开始：表示每隔10秒钟执行一次,*表示全部（每一秒）
     *
     * @Scheduled代表定时运行的注解
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void pushGoods(){

        //1.查询数据库
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //设置存库状态大于0
        criteria.andGreaterThan("stockCount",0);
        //设置审核状态  通过的状态
        criteria.andEqualTo("status","1");

        Date date = new Date();
        criteria.andLessThan("startTime",date); //开始时间
        criteria.andGreaterThan("endTime",date);//结束时间

        //根据条件查询秒杀表中的数据，需要库存大于0，还有创建时间与结束时间

        List<TbSeckillGoods> goods = seckillGoodsMapper.selectByExample(example);

       // System.out.println(goods.toString());

        //排除 已经在redis中的商品
        Set<Long> keys = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();

        if(keys!=null && keys.size()>0){
            System.out.println("Pai chu Redis!");
            //排除已经在redis中的商品
            criteria.andNotIn("id",keys);
        }


        for (TbSeckillGoods good : goods) {
            //将遍历出来的每一个商品创建成一个队列
            pushGoodsList(good);
            //2.将数据推送到redis中  SEC_KILL_GOODS作为大key 秒杀商品的id做为小key，商品作为值
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(good.getId(),good);
        }


    }

    /**
     *将每一个商品创建出一个队列 队列的元素的个数 由商品的库存决定
     * @param good   商品的id
     */
    private void pushGoodsList(TbSeckillGoods good){

        //获取并遍历商品的库存
        for (Integer i = 0; i < good.getStockCount(); i++) {
            redisTemplate.boundListOps(
                    SysConstants.SEC_KILL_GOODS_PREFIX    //代表大key的前缀标识
                            +good.getId())                    //拼接上商品的id
                    .leftPush(good.getId());                 //从左推送到商品
        }
    }
}
