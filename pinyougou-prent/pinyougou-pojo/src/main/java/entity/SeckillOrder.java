package entity;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;

import java.io.Serializable;

/**
 * ClassName: SeckillOrder
 * Description:
 * date: 2019/7/27 18:08
 *
 * @author hxq
 * @since JDK 1.8
 */
public class SeckillOrder implements Serializable {
    //秒杀订单
    private TbSeckillOrder seckillOrder;
    //秒杀商品
    private TbSeckillGoods seckillGoods;
    //秒杀订单id
    private String id;
    //秒杀商品交易流水

    public TbSeckillOrder getSeckillOrder() {
        return seckillOrder;
    }

    public void setSeckillOrder(TbSeckillOrder seckillOrder) {
        this.seckillOrder = seckillOrder;
    }

    public TbSeckillGoods getSeckillGoods() {
        return seckillGoods;
    }

    public void setSeckillGoods(TbSeckillGoods seckillGoods) {
        this.seckillGoods = seckillGoods;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
