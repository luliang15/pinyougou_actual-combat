package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.utils.HttpClient;
import com.pinyougou.pay.service.PayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:PayServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/14
 * @TIME： 10:15
 * @Description: TODO
 */
@Service
public class PayServiceImpl implements PayService {


    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 模拟浏览器发送请求 调用统一下单的APi 给微信支付系统，接收 响应 获取里面的 支付二维码的链接地址
     *
     * @param out_trade_no 生成的订单号（此二维码的订单号）
     * @param total_fee 设置支付的金额
     * @return
     */
    @Override
    public Map<String, String> createNatice(String out_trade_no, String total_fee) {

        //1.组合参数 到map中
        Map<String, String> param = new HashMap();//创建参数
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", out_trade_no);//商户订单号
        param.put("total_fee", total_fee);//总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://test.itcast.cn");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型


        try {
            //自动添加了签名的字符串xml  封装数据之后的map会自动转成xml，解析xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(xmlParam);

            //2.调用httpClient 模拟浏览器发送请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);  //可以携带参数
            client.setXmlParam(xmlParam);//请求体
            client.post();  //发送请求的按钮

            //3.调用httpClient获取响应内容，解析出里面的code_url
            String result = client.getContent();

            System.out.println(result);

            //将解析出来的二维码支付路径封装进一个微信Map
            Map<String, String> wxMap = WXPayUtil.xmlToMap(result);

            //4.返回map（订单号、金额、code_url）
            Map<String, String> resultMap = new HashMap<>();

            resultMap.put("code_url", wxMap.get("code_url"));//支付地址，二维码的支付地址
            resultMap.put("total_fee", total_fee);//总金额
            resultMap.put("out_trade_no", out_trade_no);//订单号


            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            //如果出错，返回一个空的map
            return new HashMap<>();
        }
    }

    /**
     * 检车某一个订单号的支付的状态，如果支付状态为success，则支付成功
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryStatus(String out_trade_no) {

        try {
            //1.组合参数 到map中
            Map<String, String> param = new HashMap();//创建参数
            param.put("appid", appid);//公众号
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", out_trade_no);//商户订单号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //map装换为xml时会自动添加一个签名

            //将map转换为xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);

            //2.调用httpClient 模拟浏览器发送请求  创建连接微信支付的接口连接
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true); //设置它为一个https的请求
            httpClient.setXmlParam(signedXml);  //设置body 请求体
            httpClient.post();   //发送请求

            //3.调用httpClient获取响应内容，解析出里面的支付状态trade_state
            String content = httpClient.getContent();  //此时获取到的是一个xml
            System.out.println("WeXin is xml:" + content);

            //4.最后返回map，需要将xml再转回map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            //没有异常，正常返回
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            //如果出现异常 ,就是查询不到支付状态
            return null;
        }

    }

    /**
     * 关闭微信订单（交易流水号）的方法
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> closePay(String out_trade_no) {

        try {
            //1.组合参数 到map中
            Map<String, String> param = new HashMap();//创建参数
            param.put("appid", appid);//公众号
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", out_trade_no);//商户订单号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //map装换为xml时会自动添加一个签名

            //将map转换为xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);

            //2.调用httpClient 模拟浏览器发送请求  创建连接关闭微信支付订单的接口连接
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true); //设置它为一个https的请求
            httpClient.setXmlParam(signedXml);  //设置body 请求体
            httpClient.post();   //发送请求

            //3.调用httpClient获取响应内容，解析出里面的支付状态trade_state
            String content = httpClient.getContent();  //此时获取到的是一个xml
            System.out.println("guan bi result :" + content);

            //4.最后返回map，需要将xml再转回map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            //没有异常，正常返回
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            //如果出现异常 ,就是查询不到支付状态
            return null;
        }
    }
}
