package com.pinyougou.cart.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @ClassName:UserDetailServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/11
 * @TIME： 10:46
 * @Description: TODO
 */
//自定义认证授权；类
public class UserDetailServiceImpl implements UserDetailsService {
    /**
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //不做认证，做授权

        return new User(username,"", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}
