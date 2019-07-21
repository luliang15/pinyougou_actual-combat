package com.pinyougou.es.dao;

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @ClassName:ItemDao
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 10:46
 * @Description: TODO
 */

public interface ItemDao extends ElasticsearchRepository<TbItem,Long> {
}
