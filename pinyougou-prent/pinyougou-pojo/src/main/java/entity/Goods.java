package entity;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName:Goods
 * @Author：Mr.lee
 * @DATE：2019/06/27
 * @TIME： 17:00
 * @Description: TODO
 */

//三张表的结合对象  tb_goods、 tb_goods_desc、 tb_item

public class Goods implements Serializable {

    private TbGoods goods;//商品SPU  三张表结合的组合对象

    private TbGoodsDesc goodsDesc;//商品扩展 商品描述

    private List<TbItem> itemList;//商品SKU列表	 商品的多个规格


    //getter  and setter方法......


    public Goods() {
    }

    public Goods(TbGoods goods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
        this.goods = goods;
        this.goodsDesc = goodsDesc;
        this.itemList = itemList;
    }

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
