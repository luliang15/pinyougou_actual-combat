package com.pinyougou.shop.security.config;

import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @ClassName:ShopSecurityConfig
 * @Author：Mr.lee
 * @DATE：2019/06/26
 * @TIME： 21:19
 * @Description: TODO
 */

@EnableWebSecurity  //表示配置类
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {

        //需要在xml配置 <!--自定义认证类,从数据库进行认证查询-->
    @Autowired  //注入安全框架（自动校验用户与密码）中的UserDetailsService接口
    private UserDetailsService userDetailsService;

    @Autowired   //注入密码加密器
    private PasswordEncoder passwordEncoder;

    //认证的方法
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //这里不能写死，需要从数据库查询相关的用户信息，进行认证
       // auth.inMemoryAuthentication().withUser("lee").password("{noop}1 23").roles("LEE");
        //.passwordEncoder(passwordEncoder);这里是对注册的用户密码进行加密
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }


    //授权的方法
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //注册用的也要放行
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**",
                        "/*.html","/seller/add.shtml").permitAll()

                //这个表示拥有此角色身份的用户放行所有路径
                .antMatchers("/**").hasRole("ADMIN")
                //设置所有的其他请求都需要认证通过即可 也就是用户名和密码正确即可不需要其他的角色
                .anyRequest().authenticated();

        //设置自定义表单登录页面
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login?error");

        http.csrf().disable();//关闭CSRF

        //开启同源iframe 可以访问策略
        http.headers().frameOptions().sameOrigin();

        //注销 并销毁session
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);
    }
}
