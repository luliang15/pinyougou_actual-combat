package com.pinyougou.user.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;
import java.util.List;

/**
 * @ClassName:UserDetailServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/10
 * @TIME： 20:27
 * @Description: TODO
 */
public class UserDetailServiceImpl implements UserDetailsService {


    @Reference
    private UserService userService;

    /**
     * 自定义用户的认证授权类
     *
     * @param
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //这个类只做授权，不做认证，认证交给cas服务端
        //此用户被授权之后， <intercept-url pattern="/**" access="ROLE_USER"/>可访问所有首页路径
        System.out.println("Where's it going the UserDetailServiceImp?");

        TbUser condition = new TbUser();
        condition.setUsername(username);
        TbUser tbUser = userService.select(condition).get(0);

        //两个时间大于3个月 (以30天来算)
            if (new Date().getTime()/1000 - tbUser.getLastLoginTime().getTime()/1000 > 7776000) {
                return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_LOCK"));
            }

        userService.updateUserLoginDateByUsername(username);

        return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}
