package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:ItemSearchController
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 15:00
 * @Description: TODO
 */
@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {

    //需要远程注入itemSearchService的实例化bean、使用dubbo
    @Reference
    private ItemSearchService itemSearchService;


    //根据关键字keyword查询对应的模糊查询的内容信息数据
    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map<String,Object> searchMap){
         //判断参数不为空
        if(searchMap==null){
            searchMap=new HashMap<>();
        }
        return itemSearchService.search(searchMap);
    }

}
