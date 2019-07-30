package com.pinyougou.user.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.user.service.UserService;

import entity.Result;
import entity.User;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {
    @Autowired
    private UserService userService;

    private TbUserMapper userMapper;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                criteria.andLike("username", "%" + user.getUsername() + "%");
                //criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if (StringUtils.isNotBlank(user.getPassword())) {
                criteria.andLike("password", "%" + user.getPassword() + "%");
                //criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if (StringUtils.isNotBlank(user.getPhone())) {
                criteria.andLike("phone", "%" + user.getPhone() + "%");
                //criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                criteria.andLike("email", "%" + user.getEmail() + "%");
                //criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if (StringUtils.isNotBlank(user.getSourceType())) {
                criteria.andLike("sourceType", "%" + user.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(user.getNickName())) {
                criteria.andLike("nickName", "%" + user.getNickName() + "%");
                //criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if (StringUtils.isNotBlank(user.getName())) {
                criteria.andLike("name", "%" + user.getName() + "%");
                //criteria.andNameLike("%"+user.getName()+"%");
            }
            if (StringUtils.isNotBlank(user.getStatus())) {
                criteria.andLike("status", "%" + user.getStatus() + "%");
                //criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(user.getHeadPic())) {
                criteria.andLike("headPic", "%" + user.getHeadPic() + "%");
                //criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if (StringUtils.isNotBlank(user.getQq())) {
                criteria.andLike("qq", "%" + user.getQq() + "%");
                //criteria.andQqLike("%"+user.getQq()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsMobileCheck())) {
                criteria.andLike("isMobileCheck", "%" + user.getIsMobileCheck() + "%");
                //criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsEmailCheck())) {
                criteria.andLike("isEmailCheck", "%" + user.getIsEmailCheck() + "%");
                //criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getSex())) {
                criteria.andLike("sex", "%" + user.getSex() + "%");
                //criteria.andSexLike("%"+user.getSex()+"%");
            }

        }

        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    //注入redis
    @Autowired
    private RedisTemplate redisTemplate;

    //注入mq  生产者对象
    @Autowired
    private DefaultMQProducer producer;

    @Value("${sign_name}")
    private String sign_name;

    @Value("${template_code}")
    private String template_code;


    /**
     * 1.根据手机号码，生产验证码
     * 2.将验证码 存储到redis中
     * 3.将验证码 和手机码、签名、模板的内容 作为消息体 发送消息到mq
     * 3.1 集成生成mq，加入依赖
     * 3.2 配置spring的配置文件（配置生产的对象）
     *
     * @param phone
     */
    @Override
    public void createSmsCode(String phone) {

        try {
            //1.生成6位数的随机验证码
            String code = (long) ((Math.random() * 9 + 1) * 100000) + "";

            //2.将验证码存储到redis中
            redisTemplate.boundValueOps("Register_" + phone).set(code);

            //设置验证码在redis的生命周期,24L代表存活1小时
            redisTemplate.boundValueOps("Register_" + phone).expire(24L, TimeUnit.HOURS);

            //3.打印测试  验证码的生成
            System.out.println(redisTemplate.boundValueOps("Register_" + phone).get());

            //4.使用mq生成者对象发送消息
            //消息体body中有很多内容(消息本身、手机号、模板、验证码)，需要封装，使用map对象接收
            Map<String, String> messageInfo = new HashMap<>();
            //封装消息体
            messageInfo.put("mobile", phone);  //封装电话
            messageInfo.put("sign_name", sign_name);  //封装签名
            messageInfo.put("template_code", template_code); //封装模板CODE
            messageInfo.put("param", "{\"code\":\"" + code + "\"}");  //设置json字符串的参数

            //将封装好的消息体内容转成json字符创的格式
            String jsonStr = JSON.toJSONString(messageInfo);
            /*c+f11  c+num*/
            Message message = new Message(
                    "SMS_TOPIC",  //消息主题
                    "SEND_MESSAGE_TAG",  //消息标签 tag
                    "createSmsCode",  //消息的keys 唯一标识
                    jsonStr.getBytes());  //消息体 body

            //发送消息的send
            producer.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 比对验证验证码的方法
     *
     * @param phone
     * @param smsCode
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {

        //如果手机号码与验证码有一个为空，则数据错误，返回false
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(smsCode)) {
            return false;
        }
        //往下，两个都不为空
        //获取redis中的验证码
        String code = (String) redisTemplate.boundValueOps("Register_" + phone).get();

        //与页面输入的验证码进行比对
        if (!code.equals(smsCode)) {//如果不一致
            return false;
        }
        //做判断对错的时候，一般先取反，先判断为false的情况
        return true;
    }

    @Override
    public void updateByKey(TbUser user) {
        Example example1 = new Example(TbUser.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("username", user.getUsername());
        List<TbUser> tbUsers = userMapper.selectByExample(example1);
        TbUser tbUser = tbUsers.get(0);
        user.setPassword(tbUser.getPassword());
        user.setId(tbUser.getId());
        user.setPhone(tbUser.getPhone());
        user.setCreated(tbUser.getCreated());
        user.setUpdated(tbUser.getUpdated());
        userMapper.updateByPrimaryKey(user);
    }


    @Autowired  //远程注入商品查询的实例化对象
    private TbItemMapper tbItemMapper;

    /**
     * 根据商品id获取到商品对象
     * 将商品对象存到redis总，移到我的购物车的操作
     * <p>
     * 1.商品标题
     * 2.商品规格
     * 3.商品价钱
     * 4.商品的个数
     * 5.商品图片
     * 6.商品的一个点击跳转加入购物车的按钮
     *
     * @param itemId 商品id
     * @return
     */
    @Override
    public void moveMyFavorite(Long itemId) {

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        //根据商品id获取到商品
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);

        //1.获取商品标题
        String title = tbItem.getTitle();

        //2.获取商品规格
        String spec = tbItem.getSpec();

        //3.获取商品价钱
        BigDecimal price = tbItem.getPrice();

        //4.获取商品库存数量
        Integer num = tbItem.getNum();

        //5.获取商品图片
        String image = tbItem.getImage();

        //将数据都封装到map中
        map.put("title", title);
        map.put("spec", spec);
        map.put("price", price);
        map.put("num", num);
        map.put("image", image);
        map.put("itemId", itemId);


        list.add(map);

        //将封装好的map(收藏对象)移到我的收藏，存进redis中
        redisTemplate.boundHashOps(SysConstants.MY_FAVORITE).put("Favorite", list);
    }

    /**
     * 查询所有我的收藏的页面信息展示
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> findMyFavorite() {

        //从redis中获取到收藏页面的信息做展示
        List<Map<String, Object>> favoriteList = (List<Map<String, Object>>)
                redisTemplate.boundHashOps(SysConstants.MY_FAVORITE).get("Favorite");

        return favoriteList;
    }

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    /**
     * 商品详情页面加载生成传递goodsId
     * 记录所有用户浏览过的商品详细信息到我的足迹中
     * 足迹所要获取到的数据与我的收藏相似
     *
     * @return goodsId  tb_Goods表的id
     */
    @Override
    public void findMyFootprint(Long goodsId, String userId) {

        //定义一个list容器，假设有多个浏览商品使用list容器装
        List<Map<String, Object>> list =null;

        //第一次redis中没有浏览商品缓存时，创建list容器接收数据
        list  = (List<Map<String, Object>>) redisTemplate.boundHashOps
                (SysConstants.MY_FOOTPRINT + userId).get("FootList");

        //如果redis中为空
        if (list == null) {
            //创建list容器
           list = new ArrayList<>();
        }

        //商品详情页数据封装的map容器
        Map<String, Object> map = new HashMap<>();

        //1.根据goodsId获取itemId（商品id）
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);


        //获取默认的商品id
        Long itemId = tbGoods.getDefaultItemId();

        //2.根据商品id获取商品对象
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);

        //1.获取商品标题
        String title = tbItem.getTitle();

        //2.获取商品规格
        String spec = tbItem.getSpec();

        //3.获取商品价钱
        BigDecimal price = tbItem.getPrice();

        //4.获取商品库存数量
        Integer num = tbItem.getNum();

        //5.获取商品图片
        String image = tbItem.getImage();

        //将数据都封装到map中
        map.put("title", title);
        map.put("spec", spec);
        map.put("price", price);
        map.put("num", num);
        map.put("image", image);
        map.put("itemId", itemId);

        list.add(map);

        //将用户浏览的商品详细信息从左到右的形式压入队列中，使用户的名称做标记，此用户就是该队列
        redisTemplate.boundHashOps(SysConstants.MY_FOOTPRINT + userId).put("FootList", list);

    }

    /**
     * 从redis中获取用户浏览的商品详细信息
     * 商品详细信息从左到右压入队列，取从右取，当商品为空时，则从redis中删除此商品
     *
     * @param userId
     * @return
     */
    @Override
    public List<Map<String, Object>> findAllFootprint(String userId) {

        //根据大key与用户名称标识从redis中取出list<Map>
        List<Map<String, Object>> footList = (List<Map<String, Object>>)
                redisTemplate.boundHashOps(SysConstants.MY_FOOTPRINT + userId).get("FootList");

        //判断当商品被加入购物车时，从redis中删除
        return footList;
    }



    @Override
    public List<User> userClassification() {
        Example example=new Example(TbUser.class);
        example.createCriteria().andEqualTo("sex","男");
        List<TbUser> usersMan = userMapper.selectByExample(example);
        Example example1=new Example(TbUser.class);
        example1.createCriteria().andEqualTo("sex","女");
        List<TbUser> usersWoman = userMapper.selectByExample(example1);
        int weekMan=0;
        int weekWoman=0;
        int monthMan=0;
        int monthWoman=0;
        int otherMan=0;
        int otherWoman=0;
        for (TbUser tbUser : usersMan) {
            long time = (new Date().getTime())-tbUser.getLastLoginTime().getTime();
            if (time / 86400000 <= 7) {
                weekMan++;
                continue;
            }
            if (time / 86400000 > 30) {
                monthMan++;
                continue;
            }
            otherMan++;
        }
        for (TbUser tbUser : usersWoman) {
            long time = (new Date().getTime())-tbUser.getLastLoginTime().getTime();
            if (time / 86400000 <= 7) {
                weekWoman++;
                continue;
            }
            if (time / 86400000 > 30) {
                monthWoman++;
                continue;
            }
            otherWoman++;
        }
        //进行封装
        User userMan = new User();
        userMan.setSex("男");
        Map<String, Object> mapMan = new HashMap<>();
        mapMan.put("active",weekMan);
        mapMan.put("secondaryActive",monthMan);
        mapMan.put("unActive",otherMan);
        userMan.setMap(mapMan);

        User userWoman = new User();
        userWoman.setSex("女");
        Map<String, Object> mapWoman = new HashMap<>();
        mapWoman.put("active",weekWoman);
        mapWoman.put("secondaryActive",monthWoman);
        mapWoman.put("unActive",otherWoman);
        userWoman.setMap(mapWoman);

        ArrayList<User> users = new ArrayList<>();
        users.add(userMan);
        users.add(userWoman);
        return users;
    }
}
