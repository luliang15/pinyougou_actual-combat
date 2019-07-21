package com.pinyougou.es.service;

/**
 * @ClassName:ItemService
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 10:43
 * @Description: TODO
 */
public interface ItemService  {


    /**
     * 从数据库中获取数据 导入到ES的索引库
     */
    public void ImportDataToEs();

}
