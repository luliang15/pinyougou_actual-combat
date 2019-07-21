package com.pinyougou.page.service;

/**
 * @ClassName:ItemPageService
 * @Author：Mr.lee
 * @DATE：2019/07/05
 * @TIME： 22:45
 * @Description: TODO
 */
public interface ItemPageService {
    /**、
     * 根据SKU的id去查询、并生成静态页面
     *
     * @param id
     */
    void genItemHtml(Long id);

    /**
     * 根据SkU的id去删除 生成的静态页面
     * @param longs
     */
    void deleteByIds(Long[] longs);
}
