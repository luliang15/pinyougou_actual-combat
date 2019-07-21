package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:ItemPageServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/05
 * @TIME： 22:47
 * @Description: TODO
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Value("${pageDir}")
    private String pageDir;

    @Autowired   //注入分类对象
    private TbItemCatMapper tbItemCatMapper;


    /**
     * 、
     * 根据SKU的id去查询、并生成静态页面,在安全状态审核通过的情况下调用生成静态页面的方法
     *
     * @param id
     */
    @Override
    public void genItemHtml(Long id) {

        //1.根据SPU的Id 获取到SPU的信息 （包括描述的信息）
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(id);
        //商品的描述信息
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);

        //2.调用freemarker的方法， 输出静态页面（模板 + 数据集 = html）
        genHtml("item.ftl",tbGoods,tbGoodsDesc);

    }

    /**
     * 根据SkU的id去删除 生成的静态页面
     *
     * @param longs
     */
    @Override
    public void deleteByIds(Long[] longs) {
        try {
            for (Long aLong : longs) {
             //使用apaqi 提供的文件工具类进行强制删除
                FileUtils.forceDelete(new File(pageDir+aLong+".html"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * freemarker生成静态页面的方法
     */
    private void genHtml(String templateName,TbGoods tbGoods,TbGoodsDesc tbGoodsDesc) {

        FileWriter writer=null;
        try {
            //1.创建模板文件
            //2.创建configuration对象，
            //3。构建文件所在的目录以及字符编码
            Configuration configuration = configurer.getConfiguration();

            //4.加载模板文件获取到模板对象，准备数据集
            Template template = configuration.getTemplate(templateName);

            Map map = new HashMap();
            map.put("tbGoods",tbGoods);
            map.put("tbGoodsDesc",tbGoodsDesc);

            //生成商品类型的面包屑,根据商品列表的等级id查询获取到分类对象的数据
            TbItemCat cat1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat cat2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat cat3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

            map.put("cat1",cat1.getName());
            map.put("cat2",cat2.getName());
            map.put("cat3",cat3.getName());

            //根據SPU 的id 查詢該SPU下的符合條件的所有的SKU的列表數據
            //select * from tb_item where goods_id=1 and status=1 order by is_default desc
            Example example = new Example(TbItem.class);
            //創建條件
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId",tbGoods.getId());
            criteria.andEqualTo("status",1);//狀態安全

            example.setOrderByClause("is_default desc");
            List<TbItem> skuList = tbItemMapper.selectByExample(example);

            //存儲到靜態頁面中
            map.put("skuList",skuList);


            //5.创建（写）文件流，输出
            writer = new FileWriter(new File(pageDir+tbGoods.getId()+".html"));
            template.process(map,writer );


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //6.关闭流
          if(writer!=null){
              try {
                  writer.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        }

    }
}
