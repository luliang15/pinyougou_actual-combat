package com.pinyougou.seckill.service.impl;

import java.util.*;


import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.seckill.thread.CreateOrderThread;
import entity.OrderList;
import entity.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;


import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSeckillOrderMapper;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder> implements SeckillOrderService {


    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, TbSeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public Map<String, Object> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<SeckillOrder> seckillOrders = new ArrayList<>();
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            //用户名称
            if (StringUtils.isNotBlank(seckillOrder.getUserId())) {
                criteria.andLike("userId", "%" + seckillOrder.getUserId() + "%");
                //criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }
            //商家名称
            if (StringUtils.isNotBlank(seckillOrder.getSellerId())) {
                criteria.andEqualTo("sellerId", seckillOrder.getSellerId());
                //criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
            }
            //支付状态
            if (StringUtils.isNotBlank(seckillOrder.getStatus())) {
                criteria.andLike("status", "%" + seckillOrder.getStatus() + "%");
                //criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
            }
            //支付时间
            if (seckillOrder.getPayTime()!=null) {
                criteria.andEqualTo("payTime",seckillOrder.getPayTime());
                //criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
            }
            //订单号
            if (seckillOrder.getId()!=null) {
                criteria.andEqualTo("id",seckillOrder.getId());
                //criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
            }
            //收货人
            if (StringUtils.isNotBlank(seckillOrder.getReceiver())) {
                criteria.andLike("receiver", "%" + seckillOrder.getReceiver() + "%");
                //criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
            }
            //交易流水
            if (StringUtils.isNotBlank(seckillOrder.getTransactionId())) {
                criteria.andLike("transactionId", "%" + seckillOrder.getTransactionId() + "%");
                //criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
            }
        }
        List<TbSeckillOrder> tbSeckillOrders = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(tbSeckillOrders);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        if (tbSeckillOrders == null) {
            return null;
        }
        //遍历
        for (TbSeckillOrder order : tbSeckillOrders) {
            //创建自定义存储订单对象
            SeckillOrder order1 = new SeckillOrder();
            order1.setSeckillOrder(order);
            order1.setId(String.valueOf(order.getId()));
            //根据订单中的商品id获取商品名称
            TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(order.getSeckillId());
            order1.setSeckillGoods(seckillGoods);
            seckillOrders.add(order1);
        }
        map.put("seckillOrders", seckillOrders);
        map.put("pageInfo", pageInfo);
        return map;
    }

    @Autowired  //注入redis模板对象
    private RedisTemplate redisTemplate;

    @Autowired  //输入秒杀商品对象
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired  //注入多线程订单处理类
    private CreateOrderThread createOrderThread;


    /**
     * 此方法用于创建用户抢到秒杀订单且将订单存进redis中的（预订单）
     *
     * @param id     秒杀列表的商品id
     * @param userId 抢到商品的用户id
     */
    @Override
    public void submitOrder(Long id, String userId) {

       /* //1.先从redis中根据秒杀商品的id获取秒杀商品的数据
        TbSeckillGoods seckillGoods = (TbSeckillGoods)
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(id);

        //2.判断 商品是否存在 如果商品不存在 或者库存为0 说明商品已经卖完
        if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
            //说明商品不存在了,或者已经没有库存了
            throw new RuntimeException("商品已经被抢光了");
        }*/

        //判断 用户是否存在为支付的订单 如果有   先判断是否有未支付的订单
        if (redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId) != null) {
            throw new RuntimeException("有未支付的订单！");
        }


        //判断如果用户已经在排队中（订单正在创建） 给这个用户标记
        if (redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId) != null) {
            //如果此用户正在队列中
            throw new RuntimeException("正在排队，请稍等...");
        }


        //如果没有为支付的订单再弹出一个库存
        //现在商品被压成队列，从redis中弹出队列元素（也就是商品）如果元素为null 说明已经卖完了
        //之前商品从左边推送存，现在从右边取出元素
        Object seckillGoodsId = redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + id).rightPop();
        //如果此元素为null  ,这里就是判断秒杀商品还有没有库存
        if (seckillGoodsId == null) {
            throw new RuntimeException("商品已经被抢光了");
        }


        //将在排队抢购秒杀商品的用户压入redis队列中
        redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST) //表示排队用户的在redis中的大key
                .leftPush(new SeckillStatus(
                        userId,        //传入用户id
                        id,            //商品id
                        SeckillStatus.SECKILL_queuing     //用户的抢购状态，正在排队
                ));

        //将用户储存在一个正在排队的标识
        redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId, id);


        //执行创建（多线程）订单的方法
        createOrderThread.handleCreateOrder();

    }

    /**
     * 查询订单状态，是否支付
     *
     * @param userId
     * @return
     */
    @Override
    public TbSeckillOrder getUserOrderStatus(String userId) {
        //返回订单对象
        return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER)
                .get(userId);
    }

    @Autowired
    private TbSeckillOrderMapper tbSeckillOrderMapper;

    /**
     * 如果此用户的抢购秒杀订单已支付
     * 1.从redis中获取秒杀订单
     * 2.修改此订单的支付状态为已支付
     * 3.将此订单从redis中储存到mysql中
     * 4.redis中删除此订单
     *
     * @param transaction_id 交易流水号
     * @param userId         用户id
     */
    @Override
    public void updateOrderStatus(String transaction_id, String userId) {

        //1.从redis中获取秒杀订单
        TbSeckillOrder seckillOrder = getUserOrderStatus(userId);

        //2.修改此订单的支付状态为已支付
        seckillOrder.setStatus("1");
        seckillOrder.setPayTime(new Date());  //订单支付完成时间
        seckillOrder.setTransactionId(transaction_id);   //订单交易完成产生的流水号

        //3.将此订单从redis中储存到mysql中
        tbSeckillOrderMapper.insert(seckillOrder);

        //4.redis中删除此订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
    }

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    /**
     * //2.删除redis中的该用户对应对应的为支付的订单
     * //3.恢复库存
     * //4.恢复队列的元素
     *
     * @param userId 用户id
     */
    @Override
    public void deleteOrder(String userId) {

        //2.删除redis中的该用户对应对应的为支付的订单
        TbSeckillOrder seckillOrder = getUserOrderStatus(userId);

        //3.恢复库存
        Long seckillId = seckillOrder.getSeckillId(); //订单对应的秒杀商品的id

        //从根据秒杀商品的id从redis中获取到该秒杀商品
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);

        //如果该商品不为空
        if (tbSeckillGoods != null) {
            //从数据库中获取商品的数据
            TbSeckillGoods seckillGoods = tbSeckillGoodsMapper.selectByPrimaryKey(seckillId);
            //恢复库存
            seckillGoods.setStockCount(tbSeckillGoods.getStockCount() + 1);
            //存储到redis中
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId, seckillGoods);
        } else {
            //如果不为空,库存加1
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount() + 1);
            //存储到redis中
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId, tbSeckillGoods);
        }

        //4.恢复队列的元素
        redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).leftPush(seckillId);


    }
}
