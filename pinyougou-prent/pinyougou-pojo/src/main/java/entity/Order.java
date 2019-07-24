package entity;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    //卖家对应商品表
    private TbGoods tbGoods;

    // 卖家所有订单表
    private TbOrder tbOrder;

     //对应的订单表的多种规格表
    private List<TbOrderItem> orderItemList;

    public Order() {
    }

    public Order(TbGoods tbGoods, TbOrder tbOrder, List<TbOrderItem> orderItemList) {
        this.tbGoods = tbGoods;
        this.tbOrder = tbOrder;
        this.orderItemList = orderItemList;
    }

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
