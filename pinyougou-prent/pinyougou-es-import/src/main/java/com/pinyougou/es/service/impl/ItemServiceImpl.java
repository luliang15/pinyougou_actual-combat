package com.pinyougou.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ItemService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:ItemServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 10:43
 * @Description: TODO
 */
@Service
public class ItemServiceImpl implements ItemService {


    @Autowired   //注入dao
    private ItemDao itemDao;

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 从数据库中获取数据 导入到ES的索引库
     */
    @Override
    public void ImportDataToEs() {
        //1.查询数据库中所有符合条件的SKU的数据
        TbItem tbItem = new TbItem();
        tbItem.setStatus("1");//状态正常的商品
        List<TbItem> tbItems = itemMapper.select(tbItem);

        //将规格的数据保存到map中  {"机身内存":"16G","网络":"联通3G"}
        for (TbItem item : tbItems) {
            String spec = item.getSpec();
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            item.setSpecMap(map);
        }

        //2.保存所有的数据到es服务器中
        itemDao.saveAll(tbItems);
    }
}
