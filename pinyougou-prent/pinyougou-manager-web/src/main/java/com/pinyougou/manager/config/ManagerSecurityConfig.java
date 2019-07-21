package com.pinyougou.manager.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @ClassName:ManagerSecurityConfig
 * @Author：Mr.lee
 * @DATE：2019/06/25
 * @TIME： 22:52
 * @Description: TODO
 */
@EnableWebSecurity  //表示安全框架的配置类，加上这个注解进行自动配置
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter {

    //配置认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

       //会自动添加ROLE_
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
    }

    //配置拦截与授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //拦截
        http.authorizeRequests()
                //放行登录页面和静态资源页面
                .antMatchers("/css/**", "/img/**", "/js/**",
                        "/plugins/**", "/login.html").permitAll()
                //设置所有的其他请求都需要认证通过即可 也就是用户名和密码正确即可不需要其他的角色
                .anyRequest().authenticated();

        //设置表单登录
        http.formLogin()
                .loginPage("/login.html")   //登录到的页面
                .loginProcessingUrl("/login")  //与前端对应的处理post请求的路径
                //默认跳转路径，只要登录成功，加了true就永远跳转到首页，不加true就哪里来跳回哪里去
                .defaultSuccessUrl("/admin/index.html", true)
                //显示报错信息的路径
                .failureUrl("/login?error");

        //定义注销退出登录
        //logoutUrl标识退出登录要发送的请求路径
        //invalidateHttpSession，表示清空Session
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);




        //关闭防盗链
        http.csrf().disable();//关闭CSRF




        //开启同源iframe 可以访问策略：同源，
        //sameOrigin()范围最小的
        // deny() 默认同源
        // disable()  禁掉同源，什么页面都可以访问
        http.headers().frameOptions().sameOrigin();

    }
}
