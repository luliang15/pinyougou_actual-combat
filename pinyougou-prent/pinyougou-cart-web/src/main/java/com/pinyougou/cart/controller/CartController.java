package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.utils.CookieUtil;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:CartController
 * @Author：Mr.lee
 * @DATE：2019/07/11
 * @TIME： 11:28
 * @Description: TODO
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    //使用dubbo框架远程注入一个cartService
    @Reference
    private CartService cartService;


    /**
     * 获取购物车列表中的所有数据到前端页面做展示
     * @param request
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){

        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //判断此用户是否已经登录  ,这里是匿名登录
        if("anonymousUser".equals(name)){
            //此用户未登录，从Cookie中获取购物车列表，匿名添加商品到购物车
            String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);

            //判断cookie如果为空
            if(StringUtils.isEmpty(cartListStr)){
                cartListStr = "[]";  //表示一个空的 new ArrayList集合，然后将cookie的值付给这个集合
            }

            //需要JSON字符串转为Array集合的JSON对象
            List<Cart> cartList = JSON.parseArray(cartListStr, Cart.class);

            return cartList;

        }else {
            //此用户已经登录，从redis中获取购物车列表
            List<Cart>  cartListFromRedis = cartService.findCartListFromRedis(name);
            //判断如果购物陈列表为空
            if(cartListFromRedis == null){
                cartListFromRedis = new ArrayList<>();
            }

            //在用户登录的情况下，使cookie的数据合并到redis中
            //1.获取cookie中的购物车列表数据
            String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);

            //判断cookie如果为空
            if(StringUtils.isEmpty(cartListStr)){
                cartListStr = "[]";  //表示一个空的 new ArrayList集合，然后将cookie的值付给这个集合
            }
            //需要JSON字符串转为Array集合的JSON对象
            List<Cart> cartList = JSON.parseArray(cartListStr, Cart.class);

            //2.获取redis中的购物车列表数据
            //cartListFromRedis

            //3.合并数据，返回一个最新的购物车的列表
            List<Cart> cartListNewMost = cartService.merge(cartList,cartListFromRedis);

            //4.将最新的购物车列表数据保存回redis
            cartService.saveToRedis(name,cartListNewMost);

            //5.删除cookie中的购物车数据
            CookieUtil.deleteCookie(request,response,"cartList");

            //6.返回给页面最新的购物车列表数据
            return cartListNewMost;
        }

    }




    /**
     *添加商品到已有的购物车列表中去
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    //指定访问的域来访，可以携带cookie   这就是spring框架提供的跨域访问的注解 可以放在类上，跨整个类
    @CrossOrigin(origins = {"http://localhost:9105","http://localhost:9106"},allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num,
                                     HttpServletRequest request,
                                     HttpServletResponse response){
        //表示服务器 资源允许 指定的域 来访问
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        //同意客户端携带cookie
       // response.setHeader("Access-Control-Allow-Credentials", "true");

        try {
            //根据spring-security框架获取用户名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
           // System.out.println(name);

            //判断此名称不为空的情况 anonymousUser表示匿名用户
            if ("anonymousUser".equals(name)) {
                //如果没有登录
                System.out.println("Not login!");

                //1.1从匿名点击收藏商品到购物车中Cookie中获取购物车列表，这里是去cookie
                String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);

                //判断cookie如果为空
                if(StringUtils.isEmpty(cartListStr)){
                        cartListStr = "[]";  //表示一个空的 new ArrayList集合，然后将cookie的值付给这个集合
                }

                //需要JSON字符串转为Array集合的JSON对象
                List<Cart> cartList = JSON.parseArray(cartListStr, Cart.class);

                //1.2 向已有的购物车列表中添加商品，返回一个最新的购物车列表
                List<Cart> cartListNew = cartService.addGoodsToCartList(cartList,itemId,num);
                //再判断如果购物车列表为空
                if(cartListNew==null){
                    //则将购物车列表 也是 new ArrayList
                    cartListNew = new ArrayList<>();
                }

                //1.3 将最新的购物车列表数据重新写入到Cookie中,存cookie
                CookieUtil.setCookie(request,response,
                        "cartList",
                        JSON.toJSONString(cartListNew),
                        7*3600*24,
                        true
                        );

            }else {

                //如果登录
                System.out.println("This is login!");

                //2.1从Redis中获取购物车列表,根据用户名（用户登录后添加购物车的唯一标识）
                List<Cart>  cartListFromRedis = cartService.findCartListFromRedis(name);
                //判断如果购物陈列表为空
                if(cartListFromRedis == null){
                    cartListFromRedis = new ArrayList<>();
                }


                //2.2 向已有的购物车列表中添加商品，返回一个最新的购物车列表
                List<Cart> cartListNew= cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                //判断新的购物车如果为空
                if(cartListNew == null){
                    cartListNew = new ArrayList<>();
                }


                //2.3将最新的购物车列表数据重新写入到Redis中 以key value的形式存取
                cartService.saveToRedis(name,cartListNew);

            }

            //商品成功添加
            return new Result(true,"Merchandise added successfully !!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Failed to add !!!");
        }

    }

    /**
     * 此方法用来获取用户名称在前端页面做展示
     * @return
     */
    @RequestMapping("/getName")
    public String getName(){

        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
