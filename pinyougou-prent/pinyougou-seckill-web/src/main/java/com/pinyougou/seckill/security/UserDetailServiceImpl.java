package com.pinyougou.seckill.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @ClassName:UserDetailServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/15
 * @TIME： 21:13
 * @Description: TODO
 */
public class UserDetailServiceImpl implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //授权

        return new User(username,"", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}
