package com.pinyougou.es.dao;

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @ClassName:ItemDao
 * @Author：Mr.lee
 * @DATE：2019/07/05
 * @TIME： 9:36
 * @Description: TODO
 */
public interface ItemDao extends ElasticsearchRepository<TbItem,Long> {

    //对pinyougou的下的类型为item下的所有文档进行基本 的CRUD操作
}
