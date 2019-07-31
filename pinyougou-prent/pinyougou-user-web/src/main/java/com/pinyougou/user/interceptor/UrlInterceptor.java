package com.pinyougou.user.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlInterceptor implements HandlerInterceptor {

    /****
     * 用户访问拦截过滤
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
   /* public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取用户访问的url
        String uri = request.getRequestURI();



        // 判断是否具有权限，有权限true，没有权限false
        boolean flag = false;


        // 有权限，放行
        if(flag){
            return true;
        }
        else{
            //没任何权限或者不包含当前访问地址的权限，则转发向到403
            request.getRequestDispatcher("/403.jsp").forward(request,response);
            return false;
        }
    }*/
}
