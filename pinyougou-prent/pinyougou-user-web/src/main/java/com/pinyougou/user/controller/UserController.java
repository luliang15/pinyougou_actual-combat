package com.pinyougou.user.controller;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.order.service.OrderService;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.user.service.UserService;
import entity.Error;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import entity.Result;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;


	@Reference  //注入订单实力化对象
    private OrderService orderService;


	@Reference
	private AddressService addressService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }

    /**
     *@Valid表示需要校验才能通过，走到Person,指定我们要交易的对象修饰参数
     * person 这个pojo中要加入JSR303特有的注解，进行校验
     * 如果校验不通过，BindingResult result  来显示错误信息
     * BindingResult 错误信息所存储的对象
     * @param user  页面要封装注册的用户信息
     * @param smsCode  页面传递的需要验证的验证码
     * @return   re 风格传参数
     */
	@RequestMapping("/add/{smsCode}")
	public Result add(@Valid @RequestBody TbUser user, BindingResult bindingResult, @PathVariable(name = "smsCode") String smsCode){



        try {

            //先校验输入的格式是否正确，如果错误
            if(bindingResult.hasErrors()){
                //创建格式错误显示对象
                Result result = new Result(false,"失败");
                //获取result中存储错误信息的集合
                List<Error> errorList = result.getErrorList();
                //获取格式错误的每一个字段
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                //遍历每一个错误的字段
                for (FieldError error : fieldErrors) {
                 //将每一个出现错误的字段添加进错误集合中
                    errorList.add(new Error(error.getField(),error.getDefaultMessage()));
                }
                //最后将要显示的错误信息响应给前端页面
                return result;
            }


		    //现在在注册新用户的时候首先需要先对验证码进行验证，
            //如果不正确的话，则直接返回错误信息
            boolean code = userService.checkSmsCode(user.getPhone(),smsCode);
            //判断验证码是否正确
            if(code==false){  //如果返回的是false，证明验证码不一致
                return new Result(false,"验证码输入有误！");
            }

            //如果验证码正确的情况才开始封装新用户所要注册的信息数据，走下面的情况
            //1.因为是注册新用户，在用户注册的时候就需要对密码进行加密存储
            //获取密码
            String password = user.getPassword();
            //使用md5加密
            String hexPassword = DigestUtils.md5DigestAsHex(password.getBytes());
            user.setPassword(hexPassword);


            //2.创建一些不能为空的字段，例如时间
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());

			userService.add(user);

			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbUser findOne(@PathVariable(value = "id") Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }

    /**
     * 点击发送请求(发短信)，将用户的手机号码传送给后台
     * 后台接收手机号码，调用用户服务 发送消息给mq
     * 短信服务，使用mq作为消费者来监听消息，监听到消息，发送短信给用户即可
     * @param phone
     * @return
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){

        try {
            userService.createSmsCode(phone);
            //如果验证码发送成
            return new Result(true,"请查看你的验证码");
        } catch (Exception e) {
            e.printStackTrace();
            //如果没成
            return new Result(false,"发送验证码失败！");
        }
    }

    /**
     *
     * 根据用户名查询到该用户的所有订单
     *
     */
    @RequestMapping("/findUserIdOrder")
    public Map<String,Object> findUserIdOrder(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                                    @RequestParam(value = "pageSize", defaultValue = "2", required = true) Integer pageSize){

        //根据安全框架获取到用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return null;
    }

    @RequestMapping("/addUser")
	public Result addUser(@RequestBody TbUser user,String birthday){
		try {

			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			Date parse = format.parse(birthday);
			user.setBirthday(parse);
			userService.updateByKey(user);
			return new Result(true,"信息添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"信息添加失败");
		}
	}
	//查找全部
	@RequestMapping("/findAllAddress")
	public List<TbAddress> findAllAddress(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<TbAddress> address = null;
		try {
			address = addressService.findAllByUserName(username);
			return address;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	//修改
	@RequestMapping("/updateAddress")
	public Result updateAddress(@RequestBody TbAddress address){
		try {
			addressService.update(address);
			return new Result(true,"修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true,"修改失败!");
		}
	}
	//删除
	@RequestMapping("/deleteAddress")
	public Result deleteAddress(Long id){
		try {
			addressService.deleteByPrimaryKey(id);
			return new Result(true,"删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败!");
		}
	}
	@RequestMapping("/addAddress")
	public Result addAddress(@RequestBody TbAddress address){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		address.setUserId(username);
		address.setCreateDate(new Date());
		try {
			addressService.add(address);
			return new Result(true,"添加地址成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true,"添加地址失败!");
		}
	}
	@RequestMapping("/findOneAddress/{id}")
	public TbAddress findOneAddress(@PathVariable(value = "id") Long id){
		TbAddress address = addressService.findOne(id);
		return address;
	}

}
