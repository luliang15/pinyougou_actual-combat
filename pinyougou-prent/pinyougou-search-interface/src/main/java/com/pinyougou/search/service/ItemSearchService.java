package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:ItemSearchService
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 15:05
 * @Description: TODO
 */
public interface ItemSearchService {

    /**
     * 根据输入的关键字查询对应的数据
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    /**
     *根据SKU的数据列表，将其更新到ES服务器中
     * @param itemList
     */
    void updateIndex(List<TbItem> itemList);

    /**
     * 需要类似上面的一样，根据SKU的数据列表(数组Ids),来删除ES服务中的数据
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
