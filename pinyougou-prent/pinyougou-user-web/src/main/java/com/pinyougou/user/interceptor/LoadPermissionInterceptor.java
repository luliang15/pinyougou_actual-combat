package com.pinyougou.user.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class LoadPermissionInterceptor implements HandlerInterceptor {
    /****
     * preHandle：目标方法执行前拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //用户如果已经登录，则加载用户权限；没有登录，放行。
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            //获取用户的角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if(authorities!=null && authorities.size()>0){

                //用户角色
                for (GrantedAuthority authority : authorities) {
                    System.out.println("当前登录用户具有的角色："+authority.getAuthority());
                    //使用角色名称，获取角色，从而获取角色ID
                    if ("ROLE_LOCK".equals(authority.getAuthority())){
                        request.getRequestDispatcher("lock.html").forward(request,response );
                        return false;
                    }
                }


            }
        }
        // 放行
        return true;
    }
}
