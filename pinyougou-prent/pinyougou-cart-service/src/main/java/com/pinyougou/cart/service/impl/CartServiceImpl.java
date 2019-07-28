package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:CartServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/11
 * @TIME： 20:37
 * @Description: TODO
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired  //注入tbItemMapper的映射对象，从数据库中获取查询数据
    private TbItemMapper tbItemMapper;

    @Autowired  //注入redis模板对象
    private RedisTemplate redisTemplate;

    /**
     * 向已有的购物车列表中，添加商品，返回一个新的购物车列表
     *
     * @param cartList 旧的购物车列表
     * @param itemId   表示商品的规格选项ID 要添加
     * @param num      选中的商品数量  要添加
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据商品的sku ID 获取商品的数据
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);

        //2.获取商品中的商家的id
        String sellerId = tbItem.getSellerId();

        //再根据商家id与购物车列表获取到对应的cart购物车对象
        Cart cart = findCartBySellerId(sellerId, cartList);

        //如果cart购物车对象为空
        if (cart == null) {
            //3.判断 要添加的商品，是否在已有的购物车中的 商家ID  存在 如果不存在 直接添加商品
            //创建一个空的购物车对象
            cart = new Cart();

            //此时购物车对象中没有数据，需要添加商家名称，商家id，还有对象的商品明细列表的数据
            cart.setSellerId(tbItem.getSellerId());
            cart.setSellerName(tbItem.getSeller());

            //创建明细列表的集合对象
            List<TbOrderItem> orderItemList = new ArrayList<>();

            //创建一个商品订单的明细列表对象,要添加的商品的数据所封装的pojo
            TbOrderItem orderItem = new TbOrderItem();

            //补充设置明细列表所需要的属性
            orderItem.setItemId(itemId);//商品id
            orderItem.setGoodsId(tbItem.getGoodsId());//商品的SKU的id
            orderItem.setTitle(tbItem.getTitle());//商品的标题
            orderItem.setPrice(tbItem.getPrice()); //商品价格
            orderItem.setNum(num);  // 传递过来的商品所要添加的数量

            //小计金额需要计算出来   BigDecimal是一个比double还大 的浮点类型
            double number = num * tbItem.getPrice().doubleValue();
            orderItem.setTotalFee(new BigDecimal(number));  //添加在购物车的商品小计金额
            orderItem.setPicPath(tbItem.getImage());  // 添加商品的图片路径

            //然后将所有的属性添加到明细列表的集合对象中
            orderItemList.add(orderItem);

            //再讲明细列表的数据添加到此购物车对象中
            cart.setOrderItemList(orderItemList);

            //购物车列表再添加这个购物车(商品)对象
            cartList.add(cart);

        } else {
            //此时是此购物车对象不为空的时候

            //4.判断 要添加的商 品 是否在已有的购物车中的 商家ID 存在 如果存在
            //获取已存在的商品明细列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //根据以下方法判断此明细列表中是否包含这个商品，如果包含则数量相加，如果不包含，则直接添加
            TbOrderItem tbOrderItemfind = searchByItemId(orderItemList, itemId);

            if (tbOrderItemfind == null) {
                //这里此商家的明细列表为空
                //4.1 判断 要添加的商品 是否在 商家下的明细列表中是否存在 如果不存在 直接添加商品
                //创建一个商品订单的明细列表对象,要添加的商品的数据所封装的pojo
                tbOrderItemfind = new TbOrderItem();

                //补充设置明细列表所需要的属性
                tbOrderItemfind.setItemId(itemId);//商品id
                tbOrderItemfind.setGoodsId(tbItem.getGoodsId());//商品的SKU的id
                tbOrderItemfind.setTitle(tbItem.getTitle());//商品的标题
                tbOrderItemfind.setPrice(tbItem.getPrice()); //商品价格
                tbOrderItemfind.setNum(num);  // 传递过来的商品所要添加的数量

                //小计金额需要计算出来   BigDecimal是一个比double还大 的浮点类型
                double number = num * tbItem.getPrice().doubleValue();
                tbOrderItemfind.setTotalFee(new BigDecimal(number));  //添加在购物车的商品小计金额
                tbOrderItemfind.setPicPath(tbItem.getImage());  // 添加商品的图片路径

                //将添加好属性的商品明细列表添加进已存在的商品明细列表集合中
                orderItemList.add(tbOrderItemfind);

            } else {
                //这里表示此商家的明细列表不为空

                //4.2 判断 要添加的商品 是否在 商家下的明细列表中是否存在 如果 存在 数量相加
                tbOrderItemfind.setNum(tbOrderItemfind.getNum() + num); //让原来的商品明细与将要添加的商品相加就好

                //数量改变，小计也跟着变化,也就是更新小计
                double number = tbOrderItemfind.getPrice().doubleValue() * tbOrderItemfind.getNum();

                tbOrderItemfind.setTotalFee(new BigDecimal(number));

                //如果商品移除为0的时候，直接删除这个商品
                if(tbOrderItemfind.getNum() <= 0){
                    //移除这个商品
                    orderItemList.remove(tbOrderItemfind);
                }

                //如果当所有的商品都移除没了，购物车中商家也就没有了
                if(orderItemList.size() <= 0){

                    //移除商家
                    cartList.remove(cart);
                }

            }
        }

        //此时返回一个最新的购物车列表
        return cartList;
    }

    /**
     * 根据用户名从redis中取出用户添加商品到购物车列表数据
     *
     * @param name 用户名
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String name) {

        //1.注入redis模板，调用redis模板的API，根据用户名从redis中获取购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("Redis_CartList").get(name);

        //将购物车列表返回出去
        return cartList;
    }

    /**
     * 将用户名作为key，新的购物车列表作为value的形式将数据都添加进redis中
     *
     * @param name        用户名
     * @param cartListNew 新的购物车列表
     */
    @Override
    public void saveToRedis(String name, List<Cart> cartListNew) {

        //将新的购物车列表添加进redis中
        redisTemplate.boundHashOps("Redis_CartList").put(name,cartListNew);
    }

    /**
     * 合并cookie与redis中的购物车列表的数据，全部合并到redis的购物车中
     *
     * @param cartList       cookie中的购物车列表数据
     * @param cartListFromRedis redis中的购物车列表数据
     * @return
     */
    @Override
    public List<Cart> merge(List<Cart> cartList, List<Cart> cartListFromRedis) {

        //遍历cookie中的购物车列表数据
        for (Cart cart : cartList) {
            //获取购物车的明细列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //再遍历明细列表
            for (TbOrderItem orderItem : orderItemList) {
                //orderItem就是要添加的商品对象
                cartListFromRedis = addGoodsToCartList(cartListFromRedis,
                        orderItem.getItemId(), orderItem.getNum());
            }
        }

        //最后返回一个合并好的新的购物车数据列表存进redis中
        return cartListFromRedis;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }


    /**
     * 此方法用来判断此购物车的明细列表集合中是否包含此商品
     * 在明细列表中，要添加的商品是否存在
     *
     * @param orderItemList 添加到购物车中的商品的明细列表集合
     * @param itemId        商品id
     * @return
     */
    private TbOrderItem searchByItemId(List<TbOrderItem> orderItemList, Long itemId) {

        //遍历明细列表
        for (TbOrderItem tbOrderItem : orderItemList) {
            //判断此明细列表是否包含此商品      longValue将Long转换成基本的数据类型。前面的会自动拆箱与装箱
            if (tbOrderItem.getItemId() == itemId.longValue()) { //此时是对象与对象相比，现在比较的是内存地址
                //包含此商品
                return tbOrderItem;
            }
        }
        //不包含此商品
        return null;
    }

    /**
     * 此方法用于根据商家id判断此个购物车列表中是否包含这个商家
     *
     * @param sellerId 商家的id
     * @param cartList 购物车列表
     * @return
     */
    private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {

        //遍历商品数据列表
        for (Cart cart : cartList) {
            //判断此购物车中是否有这个商家id
            if (sellerId.equals(cart.getSellerId())) {
                //如果有，返回商品数据cart对象
                return cart;
            }
        }
        //如果没有，直接返回null
        return null;
    }
}
