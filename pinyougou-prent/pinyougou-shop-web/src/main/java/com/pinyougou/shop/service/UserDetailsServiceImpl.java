package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:UserDetailsServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/06/26
 * @TIME： 22:25
 * @Description: TODO
 */
public class UserDetailsServiceImpl implements UserDetailsService {


    @Reference  //远程注入商家表信息，从而获得用户对象pojo
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //1.从数据库中查询用户的信息
        TbSeller tbSeller = sellerService.findOne(username);

        //2.判断用户的信息的逻辑
        //判断用户是否为空
        if(tbSeller==null){
            //如果没有这个用户，就不用校验了,直接返回null
            return null;
        }

        //如果有这个用户，但是这个用户没有被审核通过,也是登录不了
        if(!"1".equals(tbSeller.getStatus())){
            return null;
        }


        System.out.println("经过了自定已认证类"+username);

        //3.返回数据给spring-security的框架 自动的 （对应用户与密码）进行校验匹配
        //创建这和list容器来存放赋予好角色身份的用户
        /*List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        //这个方法为此用户添加角色身份
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));*/
        //{noop}代表揭秘
        //AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN")
        //这句话相当于上面的List<GrantedAuthority>，可直接为此用户添加赋予角色身份

        //如果不用户等于空且用户状态已被审核通过,这时安全框架自动地校验用户与密码然后匹配登录
//        return new User(username,"{noop}"+tbSeller.getPassword(),AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,ROLE_SELLER"));
        //注入了密码加密器，这里不要加"{noop}" 解密
        return new User(username,tbSeller.getPassword(),AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,ROLE_SELLER"));
    }
}
