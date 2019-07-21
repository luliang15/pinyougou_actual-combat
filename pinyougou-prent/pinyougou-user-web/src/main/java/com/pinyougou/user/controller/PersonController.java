package com.pinyougou.user.controller;

import com.pinyougou.user.pojo.Person;
import entity.Error;
import entity.Result;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName:PersonController
 * @Author：Mr.lee
 * @DATE：2019/07/09
 * @TIME： 22:24
 * @Description: TODO
 */
@RestController
@RequestMapping("/person")
public class PersonController {
    /**
     * @Valid表示需要校验才能通过，走到Person,指定我们要交易的对象修饰参数
     * person 这个pojo中要加入JSR303特有的注解，进行校验
     * 如果校验不通过，BindingResult result  来显示错误信息
     * BindingResult 错误信息所存储的对象
     * @param person
     * @param result
     * @return
     */
    @RequestMapping("/add")
    public Result add(@Valid @RequestBody Person person, BindingResult result){

            //判断如果有错误,后台验证码没通过
        if(result.hasErrors()){
            //执行显示错误信息
            List<FieldError> fieldErrors = result.getFieldErrors();//针对每一个字段的错误

            //创建Result对象，其中添加了为前端页面响应错误信息的list<Error>字段
            Result result1 = new Result(false,"There's been an error!");
            //获取list<Error>字段
            List<Error> errorList = result1.getErrorList();

            //循环遍历
            for (FieldError error : fieldErrors) {
                //将报错的字段与，该字段默认的报错信息添加进List<Error>中，去做前端页面的响应
                errorList.add(new Error(error.getField(),error.getDefaultMessage()));
            }

            //如果有错误，表示后台对验证没有校验通过，直接返回错误信息
            return result1;
        }

        //如果后台通过验证，返回ok
        return new Result(true,"Ok!");

    }
}
