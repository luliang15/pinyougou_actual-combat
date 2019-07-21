package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

/**
 * @ClassName:CartService
 * @Author：Mr.lee
 * @DATE：2019/07/11
 * @TIME： 20:34
 * @Description: TODO
 */
public interface CartService {
    /**
     * 向已有的购物车列表中，添加商品，返回一个新的购物车列表
     * @param cartList  旧的购物车列表
     * @param itemId    表示商品的规格选项ID
     * @param num   选中的商品数量
     * @return
     */

    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据用户名从redis中取出用户添加商品到购物车列表数据
     * @param name  用户名
     * @return
     */
    List<Cart> findCartListFromRedis(String name);

    /**
     * 将用户名作为key，新的购物车列表作为value的形式将数据都添加进redis中
     * @param name  用户名
     * @param cartListNew  新的购物车列表
     */
    void saveToRedis(String name, List<Cart> cartListNew);

    /**
     *  合并cookie与redis中的购物车列表的数据
     * @param cartList  cookie中的购物车列表数据
     * @param cartListFromRedis  redis中的购物车列表数据
     * @return
     */
    List<Cart> merge(List<Cart> cartList, List<Cart> cartListFromRedis);

}
