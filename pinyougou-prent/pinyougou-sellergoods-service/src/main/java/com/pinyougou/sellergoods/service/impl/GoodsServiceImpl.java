package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.sellergoods.service.GoodsService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods> implements GoodsService {


    private TbGoodsMapper goodsMapper;

    @Autowired  //注入添加商品时需要的商品描述pojo对象
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired  //注入商品分类表
    private TbItemCatMapper tbItemCatMapper;

    @Autowired  //注入商家表
    private TbSellerMapper sellerMapper;

    @Autowired  //注入品牌
    private TbBrandMapper tbBrandMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper, TbGoods.class);
        this.goodsMapper = goodsMapper;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

    /**
     *
     * @param pageNo 当前页 码
     * @param pageSize 每页记录数
     * @param goods
     * @return
     */
    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //添加一个条件，只查询没有被删除的，false代表的状态为没有被删除
        criteria.andEqualTo("isDelete",false);//只查询没有被删除的


        if (goods != null) {
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                //根据商家的名称查询显示
                criteria.andEqualTo("sellerId",  goods.getSellerId());
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
                //criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                criteria.andEqualTo("auditStatus",  goods.getAuditStatus() );
                //criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsMarketable())) {
                criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
                //criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
            }
            if (StringUtils.isNotBlank(goods.getCaption())) {
                criteria.andLike("caption", "%" + goods.getCaption() + "%");
                //criteria.andCaptionLike("%"+goods.getCaption()+"%");
            }
            if (StringUtils.isNotBlank(goods.getSmallPic())) {
                criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
                //criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsEnableSpec())) {
                criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
                //criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
            }

        }
        //查询过后的分页展示
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    /**
     * 三个表结合的pojo
     *
     * @param goods
     */
    @Override
    public void add(Goods goods) {
  //1.获取SPU的数据
        TbGoods tbGoods = goods.getGoods();

        //添加审核状态，默认就是未审核
        tbGoods.setAuditStatus("0");
        //再设置一个不删除的状态
        tbGoods.setIsDelete(false);

        //2.获取SPU对应的描述的数据
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();

        //3.获取SKU的列表数据
        List<TbItem> itemList = goods.getItemList();

        //4.插入到三个表中,根据上面三个条件进行添加操作
        goodsMapper.insert(tbGoods);//添加商品，三个组合对象

        //设置描述表的主键
        goodsDesc.setGoodsId(tbGoods.getId());


        goodsDescMapper.insert(goodsDesc);

        //调用新增组合对象的
        saveItems(tbGoods, goodsDesc, itemList);
    }

    /**
     * 新增组合商品的方法
     * @param tbGoods
     * @param goodsDesc
     * @param itemList
     */
    private void saveItems(TbGoods tbGoods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {

        //判断如果为1就是启用规格
        if("1".equals(tbGoods.getIsEnableSpec())){

            System.out.println("有规格的添加！！！");
            //1.如果启用规格
            //TODO  将来需要添加的，将来再做，对商品的SKU列表数据的添加
            for (TbItem tbItem : itemList) {

                //设置title  SPU名称+""+规格得选项名
                String title = tbGoods.getGoodsName();

                //商品价格
                tbItem.setPrice(tbGoods.getPrice());

                //从tbItem表中获取到spec字段得属性值 ，做添加使用
                String spec = tbItem.getSpec();//{"网络":"移动3G","机身内存":"32G"}

                //因为此时取到得字段还是json字符串，需要转换成json对象,并使用map接收这些数据
                Map<String,String> map = JSON.parseObject(spec, Map.class);

                //遍历这个map
                for (String key : map.keySet()) {
                    //开始拼接字符串
                    title+=" "+map.get(key);
                }
                //将title设置进去
                tbItem.setTitle(title);

                //设置图片地址
                //[{"color":"红色","url":"http://192.168.25.129/group1/M00/00/01/wKgZhVmHINKADo__AAjlKdWCzvg874.jpg"}
                //数据库中存储图片得地址形式是list<map>得形式
                List<Map> maps = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
                //取出的：
                String images  = maps.get(0).get("url").toString();
                String image = goodsDesc.getItemImages();

                //设置第三级类目判断id
                tbItem.setCategoryid(tbGoods.getCategory3Id());//这里是设置
                //获取到3级类目的数据对象
                TbItemCat cat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
                tbItem.setCategory(cat.getName());

                //设置时间
                tbItem.setCreateTime(new Date());//这里设置好了时间
                tbItem.setUpdateTime(tbItem.getCreateTime());

                //设置外键
                tbItem.setGoodsId(tbGoods.getId());

                //设置商家id
                tbItem.setSellerId(tbGoods.getSellerId());
                //设置商家店铺名
                TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
                tbItem.setSeller(tbSeller.getNickName());

                //设置品牌
                TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
                tbItem.setBrand(tbBrand.getName());

                //添加到数据库
                itemMapper.insert(tbItem);

            }

        }else {

            //.如果不启用规格,保存的时候，SKU列表还是需要有数据的，只不过需要的是单品数据，
            System.out.println("没有规格的添加");
               //不用也没有那么多的规格选项
            //1.创建SKU商品规格列表对象
            TbItem tbItem = new TbItem();

            //补充属性的值，将数据全部插入
            //设置title,title现在没有，获取到SPU名就行
            tbItem.setTitle(tbGoods.getGoodsName());

            //商品价格
            tbItem.setPrice(tbGoods.getPrice());
            System.out.println("商品价格："+tbGoods.getPrice());

            //商品库存，因为tbGoods数据库没有这个字段，写一个默认的，写死的
            tbItem.setNum(999);

            //设置商品状态，默认正常的
            tbItem.setStatus("1");
            //默认展示
            tbItem.setIsDefault("1");
            //SPU的ID
            tbItem.setGoodsId(tbGoods.getId());
            //设置规格，现在没有规格，设置空值
            tbItem.setSpec("{}");//代表空值

            //设置图片
            List<Map> maps = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
            //取出的：
            String images  = maps.get(0).get("url").toString();
            String image = goodsDesc.getItemImages();

            //设置(商品的分类iD)第三级类目判断id
            tbItem.setCategoryid(tbGoods.getCategory3Id());//这里是设置
            TbItemCat cat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            tbItem.setCategory(cat.getName());

            //设置创建时间
            tbItem.setCreateTime(new Date());//这里设置好了时间
            tbItem.setUpdateTime(tbItem.getCreateTime());

            //设置商家id
            tbItem.setSellerId(tbGoods.getSellerId());
            //设置商家店铺名
            TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
            tbItem.setSeller(tbSeller.getNickName());

            //设置品牌
            TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
            tbItem.setBrand(tbBrand.getName());

            //将数据添加到数据库
            itemMapper.insert(tbItem);
        }
    }


    /**
     * 自已定义的一个根据id获取查询整个组合对象的数据信息
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {

        //1.根据商品id获取tbGoods 的SPU数据信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //2.根据商品id获取tbGoodsDesc 的数据信息
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

        //3.根据商品id获取 SKU列表的数据信息
        TbItem record = new TbItem();
        record.setGoodsId(id);
        List<TbItem> items = itemMapper.select(record);
        //select * from tb_item where goodsId = 1;

        //将所有的查询出来的数据添加进组合对象中
        Goods goods = new Goods();

        goods.setGoods(tbGoods);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(items);


        return goods;
    }


    /**
     * 根据id修改组合对象
     * 更新
     * @param goods
     * @return
     */
    @Override
    public void update(Goods goods) {

        //1.获取SPU的数据，然后更新
        TbGoods tbGoods = goods.getGoods();
        //更新的时候要重新设置回0
        tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);

        //2.获取商品描述的数据，然后更新
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(goodsDesc);

        //3.获取SKU的数据，然后更新  先删除原本数据库的该SKU，再添加页面传递过来的SKU数据
        //delete from tb_item where goodsId=2,根据条件删除
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId",tbGoods.getId());
        int i = itemMapper.deleteByExample(example);


        //新增
        List<TbItem> itemList = goods.getItemList();
        saveItems(tbGoods,goodsDesc,itemList);
    }

    /**
     * 自己定义的一个批量id去更新、修改商品状态的方法
     *
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {

        //update tb_goods set audit_status = 1

        //根据条件去审核修改商品的安全状态
        Example exmaple = new Example(TbGoods.class);
        exmaple.createCriteria().andIn("id",Arrays.asList(ids));

        //传入要修改的值
        TbGoods tbGoods = new TbGoods();
        tbGoods.setAuditStatus(status);
        goodsMapper.updateByExampleSelective(tbGoods,exmaple);

    }

    /**
     * 根据SPU(主键的数组)的ids数据对象查询所有的该商品的列表SKU数据
     *
     * @param ids
     * @return
     */
    @Override
    public List<TbItem> findTbItemListByIds(Long[] ids) {

        //select * from tb_item where goods_id in (1,2,3)
        //根据数组对象ids的条件进行查询SKU列表的数据信息
        Example example = new Example(TbItem.class);
        //创建查询条件
        Example.Criteria criteria = example.createCriteria();
        //根据item表中的goods_id的字段进行查询,数据类型的参数需要转换
        criteria.andIn("goodsId",Arrays.asList(ids));
        //判断此商品的安全状态是否可以上架，业务需要
        criteria.andEqualTo("status",1);

        return itemMapper.selectByExample(example);

    }

    /**
     * 根据主键ID删除对应的pojo对象
     *批量删除，逻辑删除
     * 逻辑删除的点击按钮，逻辑删除，数据不能根本地删除某个字段的值，只能修改某个字段的状态
     * @param ids
     */
    @Override
    public void delete(Object[] ids) {

        //逻辑删除，不会删除数据库某个字段的根本值，只能修改某个字段的状态，0表示为删除，1表示已删除
        //update tb_goods set is_delete=1 where id in (1,2,3)

        //根据传入的这些id作为条件去删除，修改字段的状态
        Example exmaple = new Example(TbGoods.class);
        exmaple.createCriteria().andIn("id",Arrays.asList(ids));

        //更新后的值
        TbGoods tbGoods = new TbGoods();
        //修改是否删除字段的状态  ,true代表删除
        tbGoods.setIsDelete(true);
        goodsMapper.updateByExampleSelective(tbGoods,exmaple);

    }
}
