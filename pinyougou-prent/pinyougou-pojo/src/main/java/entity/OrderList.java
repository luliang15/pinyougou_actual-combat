package entity;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.util.List;

/**
 * ClassName: OrderList
 * Description:
 * date: 2019/7/25 20:07
 *
 * @author hxq
 * @since JDK 1.8
 */
public class OrderList {
    private TbOrder order; //订单
    private List<TbOrderItem> orderItems;  //订单选项  一个订单有多个选项
    private TbGoods goods; //商品一个订单对应一个商品

    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<TbOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TbOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }
}
