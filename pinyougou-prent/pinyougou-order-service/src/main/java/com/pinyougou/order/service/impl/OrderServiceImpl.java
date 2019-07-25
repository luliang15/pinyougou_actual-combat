package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName:OrderServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/13
 * @TIME： 14:06
 * @Description: TODO
 */

@Service    //这里不需要集成coreService
public class OrderServiceImpl implements OrderService {

    @Autowired  //注入订单明细列表Mapper
    private TbOrderItemMapper orderItemMapper;

    @Autowired   //注入商品Mapper
    private TbItemMapper itemMapper;

      @Autowired  //注入redis模板对象
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired  //注入雪花算法的对象
    private IdWorker idWorker;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 提交订单的方法
     *1.订单号不能重复
     * 2.拆单
     * @param tbOrder
     */
    @Override   //这个tbOrder是页面传过来的，是一个大的订单，需要进行拆弹支付
    public void add(TbOrder tbOrder) {

        //从redis中获取购物车列表
        List<Cart> cartList = (List<Cart>)
                redisTemplate.boundHashOps("Redis_CartList").get(tbOrder.getUserId());

        double total_fee = 0;  //创建总金额
        List<String> orderList =  new ArrayList<>();  //创建订单列表，获取订单号
        //遍历购物车列表先，这里就是拆单，每一个cart（购物车对象）就是一个订单
        for (Cart cart : cartList) {
            //1.获取订单的数据  插入到订单列表中
            //1.1使用雪花算法生成订单Id
            long orderId = idWorker.nextId();

            orderList.add(orderId+"");

            //新创建一个订单对象，拆单，拆单支付
            TbOrder order = new TbOrder();

            order.setOrderId(orderId);//订单ID

            order.setUserId(tbOrder.getUserId());//用户名
            order.setPaymentType(tbOrder.getPaymentType());//支付类型,页面选择，页面传递
            order.setStatus("1");//状态：未付款

            order.setCreateTime(new Date());//订单创建日期
            order.setUpdateTime(new Date());//订单更新日期

            order.setReceiverAreaName(tbOrder.getReceiverAreaName());//地址
            order.setReceiverMobile(tbOrder.getReceiverMobile());//手机号

            order.setReceiver(tbOrder.getReceiver());//收货人
            order.setSourceType(tbOrder.getSourceType());//订单来源
            order.setSellerId(cart.getSellerId());//商家ID

            //获取购物车明细列表的数据
            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            //循环购物车明细
            double money=0;
            for (TbOrderItem orderItem : orderItemList) {
                //2.获取订单选项的数据  订单选项表
                long orderItemId = idWorker.nextId();
                orderItem.setId(orderItemId);
                //订单ID
                orderItem.setOrderId(orderId);
                //商家id
                orderItem.setSellerId(cart.getSellerId());

                TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());//商品
                orderItem.setGoodsId(item.getGoodsId());//设置商品的SPU的ID

                //订单的金额累加
                money+=orderItem.getTotalFee().doubleValue();//金额累加
                orderItemMapper.insert(orderItem);
            }


            //计算总金额 ，日志要记录的总金额 等于 之前算好的某个订单要支付的小计总金额
            total_fee+=money;//此时是一个元的单位

            //最后设置支付金额
            order.setPayment(new BigDecimal(money));

            ////提交订单
            orderMapper.insert(order);
        }

        //在用户添加订单的时候为这个交易创建支付日志记录,创建记录支付日志对象
        TbPayLog payLog = new TbPayLog();

        //补全属性
        String outTradeNo=  idWorker.nextId()+"";//支付订单号
        payLog.setOutTradeNo(outTradeNo);//支付订单号
        payLog.setCreateTime(new Date());//创建时间
        double v = total_fee * 100;  //元的单位转分的单位
        payLog.setTotalFee((long)v);   //记录总金额
        payLog.setTradeState("0");  //未支付状态
        payLog.setPayType("1");   //支付的方式 0 支付宝，1 微信支付 2 银行
        payLog.setOrderList(orderList.toString().replace(
                "[","")
                .replace("]",
                        ""));   //设置关联的订单号

        payLog.setUserId(tbOrder.getUserId());//用户ID

        //再存到redis中，可以不用  大key是payLog记录日志的表名
        redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).put(tbOrder.getUserId(),payLog);


        //在用户创建订单的时候开始添加创建记录支付日志的信息
        payLogMapper.insert(payLog);


        //这时，如果订单提交了，就需要把redis中的某一个用户的购物车订单清空
        redisTemplate.boundHashOps("Redis_CartList").delete(tbOrder.getUserId());


    }

    /**
     * 根据用户名从订单层获取存入redis中的记录支付日志对象
     *
     * @param userId
     * @return
     */
    @Override
    public TbPayLog getPayLogFromRedis(String userId) {

        //根据key获取value
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).get(userId);

        return  payLog;
    }

    /**
     * //1.更新支付日志的记录（交易的流水、交易的状态、交易的时间）
     * //2.更新日志支付的记录  关联订单的支付状态和支付时间
     * //3.删除该用户redis中的支付日志
     *
     * @param transaction_id 微信支付订单号
     * @param out_trade_no   商户订单号
     */
    @Override
    public void updateOrderStatus(String transaction_id, String out_trade_no) {

        //1.根据微信订单号，主键查询，查询到日志记录，修改日志状态
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);


        payLog.setPayTime(new Date());  //设置新的交易时间
        payLog.setTradeState("1");    //设置新的交易状态
        payLog.setOutTradeNo(out_trade_no);  //设置新的交易号

        //修改更新日志
        payLogMapper.updateByPrimaryKey(payLog);

        //2.更新 支付的日志记录 关联到的 订单的 状态 和支付时间  修改更新订单的
        String orderList = payLog.getOrderList();  //获取到订单列表

        //再切割出 订单号
        String[] split = orderList.split(",");

        //获取订单号对应的对象
        for (String orderId : split) {
            //获取到的订单对象  需要long类型
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));

            //更新订单对象的支付状态
            tbOrder.setStatus("2");//已付款的状态是2

            tbOrder.setUpdateTime(new Date());
            tbOrder.setPaymentTime(tbOrder.getUpdateTime());

            orderMapper.updateByPrimaryKey(tbOrder);
        }

        //3.删除该用户redis中的支付日志
        redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).delete(payLog.getUserId());

    }

    /**
     * 根据商家名 查询某一段时间内 每天的销售额
     * @param startTime
     * @param endTime
     * @param sellerId
     * @return
     */
    @Override
    public Map<String, Object> findSellInOneTime(String startTime, String endTime, String sellerId) {

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sellerId",sellerId );
        criteria.andEqualTo("status", "2");
        criteria.andGreaterThanOrEqualTo("paymentTime", startTime);
        criteria.andLessThanOrEqualTo("paymentTime", endTime);
        example.orderBy("paymentTime").asc();
        List<TbOrder> tbOrderList = orderMapper.selectByExample(example);

        HashMap<String, Object> map = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Double> moneyList = new ArrayList<>();
        if (tbOrderList != null && tbOrderList.size()>0) {
            for (TbOrder order : tbOrderList) {
                Date paymentTime = order.getPaymentTime();
                String dateStr = dateFormat.format(paymentTime);

                Double aDouble = (Double) map.get(dateStr);
                if (aDouble == null){
                    aDouble = 0D;
                    dateList.add(dateStr);
                }

                map.put(dateStr, aDouble + order.getPayment().doubleValue());


            }
        }

        for (String s : dateList) {
            moneyList.add((Double) map.get(s));
        }

        map.clear();
        map.put("days", dateList);
        map.put("money", moneyList);
        return map;
    }

    @Override
    public List<TbOrder> findAll() {
        List<TbOrder> tbOrders = orderMapper.select(null);
        return tbOrders;

    }

    /**
     * 根据用户名查询用户的订单列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<Map> findUserIdOrder(String userId) {

        List<Map> list = new ArrayList<>();


        //.1查出用户的订单列表
        TbOrder tbOrder = new TbOrder();
        tbOrder.setUserId(userId);

        //获取到第一级的订单列表
        List<TbOrder> tbOrders = orderMapper.select(tbOrder);

       // 遍历大的订单列表
        for (TbOrder order : tbOrders) {

            //第二级的订单集合
            List<Map> orderList =  new ArrayList<>();

            HashMap map = new HashMap<>();

            //直接封装order对象，order对象中包括有：订单创建时间、订单编号、店铺的名称、订单的支付状态、订单价格
            map.put("order",order);


            TbOrderItem tbOrderItem = new TbOrderItem();
            //将orderId设置为查询条件
            tbOrderItem.setOrderId(order.getOrderId());

            //根据订单id获取到订单item的对象
            List<TbOrderItem> tbOrderItems = orderItemMapper.select(tbOrderItem);


            for (TbOrderItem orderItem : tbOrderItems) {

                //获取商品的图片地址
                String picPath = orderItem.getPicPath();
                map.put("picPath",picPath);

                //获取商品的标题
                String title = orderItem.getTitle();
                map.put("title",title);

                //获取商品的id
                Long itemId = orderItem.getItemId();
                map.put("itemId",itemId);

                //商品个数
                Integer num = orderItem.getNum();
                map.put("num",num);

                //根据itemId获取到sku
                TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);

                //获取商品的规格信息
                String spec = tbItem.getSpec();
                map.put("spec",spec);

                orderList.add(map);
            }

            //最后将封装好的map返回出去
           list.add(map);
        }


        return list;
    }


}
