package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:ItemSearchServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 15:05
 * @Description: TODO
 */
@Service  //注入dubbo的远程注入
public class ItemSearchServiceImpl implements ItemSearchService {



    @Autowired   //注入全文搜索的模版实例化对象
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 根据输入的关键字查询对应的数据，这里是一个很大的业务查询，基本上首页的全部信息都是根据这个方法查询到的数据做展示的
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {


        //创建一个map对象，查询出来的所有数据都以map对象形式存进
        Map<String,Object> resultMap = new HashMap<>();


        //1.获取关键字
        String keywords = (String) searchMap.get("keywords");

        //2.创建搜索查询对象 的构建对象
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //3.创建并添加查询条件 匹配查询  先分词再查询
        //使用多字段进行高亮查询 multiMatchQuery 多字段查询的API
        //有好几个参数，第一个代表要搜索的文本,"text",后面的代表要从那个字段进行搜索
        builder.withQuery(QueryBuilders.multiMatchQuery(keywords,"title","seller","brand","category"));
        builder.withIndices("pinyougou");  //根据pinyougou索引下去查询的

        //要高亮的话，还需指定高亮标签的前缀与后缀
        //3.1设置高亮显示的 域（字段）设置前缀与后缀

        //设置前缀与后缀
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<em style=\"color:red\">").postTags("</em>");
        builder    //设置要高亮的字段
                .withHighlightFields(new HighlightBuilder.Field("title"))
                .withHighlightBuilder(highlightBuilder);



        //3.2设置聚合查询的条件  根据商品的分类来聚合查询  根据分类来进行分组field("category")指定分组的字段
        builder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));


        //3.3过滤查询的，属于多条件查询，使用boolQuery()
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


        //3.3.1商品分类的过滤查询

        //获取要过滤的字段（分类字段），这个是页面传过来的
        String category = (String) searchMap.get("category");

        //此处必须做一个非空判断，假如在搜索框架什么都不输入的情况下点击搜索
        if(StringUtils.isNotBlank(category)){
            //termQuery(词条)查询分类管理字段  第一个参数表示分类字段，第二个参数表示输入的要过滤的对象
            //此条件作为boolQuery的子条件去进行根据多条件的查询，，filter就是过滤
           boolQuery.filter( QueryBuilders.termQuery("category", category));
        }

        //3.3.2 商品的品牌的过滤查询
        //获取要过滤的品牌字段
        String brand = (String) searchMap.get("brand");

        if(StringUtils.isNotBlank(brand)){
            //termQuery(词条)查询分类管理字段  第一个参数表示分类字段，第二个参数表示输入的要过滤的对象
            //此条件作为boolQuery的子条件去进行根据多条件的查询，，filter就是过滤
            boolQuery.filter( QueryBuilders.termQuery("brand", brand));
        }

        //3.3.3 规格的过滤查询 获取规格的名称 和规格的值  执行过滤查询
        //此时获取到这样的一个数据格式: {"网络":"移动4G","机身内存":"16G"}
        Map<String,String> spec = (Map<String, String>) searchMap.get("spec");

        //非空判断
        if(spec!=null){
              //进行循环遍历
            for (String key : spec.keySet()) {
                //根据key获取值的方式获取值，"specMap.机身内存.keyword"(规格key)，对应的值就是 spec.get(key)g规格值
                boolQuery.filter( QueryBuilders.termQuery("specMap."+key+".keyword", spec.get(key)));
            }
        }


        //3.4价格区间的过滤，范围查询

        //获取点击价格时的参数price
        String price = (String) searchMap.get("price");
        //判断如果  价格不为空
        if(StringUtils.isNotBlank(price)){

            //字符串截取  什么到什么之间
            String[] split = price.split("-");

            if("*".equals(split[1])){
                //当与*匹配时，*代表所有。就是大于某一个价格
                //rangeQuery范围查询的API。查询的是price的字段，gte代表大于等于这个数 ，这里的split[0]  3000~*
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }else {
                //表示从哪里查询到哪里   1<=price<=5
                boolQuery.filter(QueryBuilders.rangeQuery("price").from(split[0],true).to(split[1],true));
            }
        }


        //查询对象进行查询
        builder.withFilter(boolQuery);


        //4.构建查询对象
        NativeSearchQuery searchQuery = builder.build();


        //4.1设置分页查询，页面传两个参数，一个当前页码的参数，一个每页显示的行数的参数
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        //判断如果两个参数在为空的时候，默认值
        if(pageNo==null){pageNo=1;}
        if(pageSize==null){pageSize=40;}
        //第一个参数为当前页码，如果为0表示第一页
        //第二个参数为 每页显示的行
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        searchQuery.setPageable(pageable);

        //4.2按照价格来排序 定义两个变量 sortType:要排序的类型 ASC DESC sortField：接收要排序的字段,如：价格、
        //获取两个字段的参数
        String sortType = (String) searchMap.get("sortType");
        String sortField = (String) searchMap.get("sortField");  //如：price

        //判断两个字段都不为空的情况下
        if(StringUtils.isNotBlank(sortType) && StringUtils.isNotBlank(sortField)){
            //进行是升序或者降序或者不用排序的判断
            if("ASC".equals(sortType)){
                Sort sort = new Sort(Sort.Direction.ASC, sortField);//order by price asc
                //进行升序的查询
                searchQuery.addSort(sort);
            }else if("DESC".equals(sortType)){
                Sort sort = new Sort(Sort.Direction.DESC, sortField);//order by price desc
                //进行降序的查询
                searchQuery.addSort(sort);
            }else {
                //不用排序
                System.out.println("不排序");
            }
        }


        //5.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class, new SearchResultMapper() {

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {

                //1.获取结果集
                SearchHits hits = response.getHits();
                List<T> content = new ArrayList<>();


                //如果没有搜索到记录
                if(hits==null || hits.getHits().length<=0){
                    return new AggregatedPageImpl(content);
                }
                 //高亮查询

                //3.获取查询的总记录数
                for (SearchHit hit : hits) {
                    //就是每一个文档对应的json数据
                    String sourceAsString = hit.getSourceAsString();

                    TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);

                    //System.out.println(";;;;;"+sourceAsString);

                    //获取高亮
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();

                    //获取高亮的域为title的高亮对象的数据
                    HighlightField highlightField = highlightFields.get("title");

                    if(highlightField!=null && highlightFields.get("title")!=null && highlightFields.get("title").getFragments()!=null) {

                        //获取高亮的碎片
                        Text[] fragments = highlightField.getFragments();
                        //高亮的数据
                        StringBuffer sb = new StringBuffer();

                        if (fragments != null) {
                            for (org.elasticsearch.common.text.Text fragment : fragments) {
                                //获取到的高亮碎片的值<em styple="colore:red">
                                sb.append(fragment.string());
                            }
                        }

                        //不为空的时候 存储值
                        if(StringUtils.isNotBlank(sb.toString())){
                            tbItem.setTitle(sb.toString());
                        }

                        //System.out.println(">>>>>>"+tbItem.getTitle());
                    }

                    //将数据存储到我们的集合中
                    content.add((T)tbItem);
                }

                AggregatedPageImpl aggregatedPage = new AggregatedPageImpl<T>(content,pageable,hits.getTotalHits(),response.getAggregations(),response.getScrollId());

                return aggregatedPage;
            }
        });


        //6.获取结果集  返回

        //6.1获取分类 分组查询的结果
        Aggregation category_group = tbItems.getAggregation("category_group");
        StringTerms stringTerms = (StringTerms)category_group;

        //6.3获取buckets中的key
        List<String> categoryList = new ArrayList<>();
        if(stringTerms!=null ){
            //6,2获取buckets，就是装有许多分类数据的水桶
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();

            for (StringTerms.Bucket bucket : buckets) {
                categoryList.add(bucket.getKeyAsString());//就是商品分类的名称   平板电视  手机
            }
        }

        //6.5将数组存储到返回结果中Map
        resultMap.put("categoryList",categoryList);

       // System.out.println("看看分类分组是什么东西："+category_group);



        //6.6从redis中根据商品分类名称，获取规格列表和品牌列表的数据，返回map对象中给页面展示

        //判断商品分类是否为空，如果不为空，根据点击到的商品分类查询，该分类下的所有的品牌和规格列表
        if(StringUtils.isNotBlank(category)){
            //根据点击的商品分类查询
            Map<String,Object> maps= searchBrandAndSpecList(category);
            resultMap.putAll(maps);//添加进返回i结果集
        }else {
            //这里需要做一个非空判断
            if(categoryList!=null && categoryList.size()>0){
                //否则查询默认值，获取第一个商品分类的品牌列表和规格列表
                Map<String,Object> maps= searchBrandAndSpecList(categoryList.get(0));//map中已经有品牌列表与规格列表、
                resultMap.putAll(maps);//添加进返回i结果集
            }else {
                //这是点击商品分类为空时的情况
                //存进品牌列表得数据
                resultMap.put("brandList",new HashMap<>());
                //存进规格列表得数据
                resultMap.put("specList",new HashMap<>());
            }
        }




        List<TbItem> itemList = tbItems.getContent(); //存储到的当前页的当前数据 List<TbItem> itemList
        long totalElements = tbItems.getTotalElements();//总记录数
        int totalPages = tbItems.getTotalPages();//总页数
        resultMap.put("rows",itemList);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);

        return resultMap;

    }

    @Autowired   //注入dao
    private ItemDao itemDao;

    /**
     * 根据SKU的数据列表，将其更新到ES服务器中
     *
     * @param itemList
     */
    @Override
    public void updateIndex(List<TbItem> itemList) {

        //1.编写一个dao
        //2调用dao的saveAll方法 数据保存更新到ES服务器中
        //此时Tbitem表的pojo中海油一个字段叫specMap的中与数据库表没有做映射的，但是specMap中是规格数据
        //这些数据在同步更新之后也需要做展示、

        //循环遍历  将规格的数据存储到specMap中
        for (TbItem tbItem : itemList) {
            //获取规格数据  {"机身内存":"16G","网络":"联通4G"}
            String spec = tbItem.getSpec();
            //json形式需要转换
            Map map = JSON.parseObject(spec, Map.class);
            //再存进tbitem中
            tbItem.setSpecMap(map);
        }
        


        itemDao.saveAll(itemList);
    }

    /**
     * 需要类似上面的一样，根据SKU的数据列表(数组Ids),来删除ES服务中的数据
     *
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        //delete from tb_item where goods_id in (1、2、3)

        //创建一个ES的删除对像
        DeleteQuery deleteQuery = new DeleteQuery();
        //要 根据要删除的字段，与ids数组
        deleteQuery.setQuery(QueryBuilders.termsQuery("goodsId",ids));
        //确定删除的索引与要删除的类型
        elasticsearchTemplate.delete(deleteQuery,TbItem.class);

    }


    @Autowired  //注入redis模板
    private RedisTemplate redisTemplate;

    /**
     *
     * @param category
     * @return
     */
    private Map<String, Object> searchBrandAndSpecList(String category) {

        //1.根据商品分类的名称  获取模板的id
        Long typeTemplateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        //2.根据模板的id 获取品牌列表,存的时候怎么存，取得时候就怎么取
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeTemplateId);

        //3.根据模板的id获取 获取规格的列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeTemplateId);

        //4.存到map中。返回
        Map brandAndSpecMap = new HashMap<>();
        //存进品牌列表得数据
        brandAndSpecMap.put("brandList",brandList);
        //存进规格列表得数据
        brandAndSpecMap.put("specList",specList);

        return brandAndSpecMap;

    }
}
