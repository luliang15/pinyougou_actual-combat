package com.pinyougou.user.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.Result;
import entity.User;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService extends CoreService<TbUser> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser User);

    /**
     * 1.根据手机号码，生产验证码
     * 2.将验证码 和手机码、签名、模板的内容 作为消息体 发送消息到mq
     * @param phone
     */
    void createSmsCode(String phone);

    /**
     * 比对验证验证码的方法
     * @param phone
     * @param smsCode
     * @return
     */
    boolean checkSmsCode(String phone, String smsCode);

    void updateByKey(TbUser user);

    //设置我的收藏的信息
    void moveMyFavorite(Long itemId);

    /**
     * 查询所有我的收藏的页面信息展示
     * @return
     */
    List<Map<String, Object>> findMyFavorite();

	List<User> userClassification();
}
