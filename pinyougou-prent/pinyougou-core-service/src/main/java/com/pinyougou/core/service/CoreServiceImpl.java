package com.pinyougou.core.service;

import com.pinyougou.pojo.TbSpecification;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName:CoreServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/06/24
 * @TIME： 15:28
 * @Description: TODO
 *
 * 抽象实现类，实现前边的核心接口方法，但此实现类必须是抽象的
 * 也要给定一个泛型，泛型就是去指定可实现的pojo，例如：TbBrand品牌列表
 */
public abstract class CoreServiceImpl<T> implements CoreService<T>{

    //受保护的 继承的时候可以使用到的
    protected Mapper<T> baseMapper;

    //反射
    protected Class<T> clazz;

    @Override
    public List<T> findAll() {
        return selectAll();
    }

    @Override
    public void add(T record) {
        baseMapper.insert(record);
    }

    /**
     * 根据主键ID删除对应的pojo对象
     * @param ids
     */
    @Override
    public void delete(Object[] ids) {
        Example example = new Example(clazz);
        Example.Criteria criteria = example.createCriteria();

        //使用反射获取到的字段
        //获取字段
        Field[] declaredFields = clazz.getDeclaredFields();

        String id="id";//主键属性的名称先默认为ID
        for (Field declaredField : declaredFields) {

            if(declaredField.isAnnotationPresent(Id.class)){//注解存在
                id = declaredField.getName();
                break;
            }
        }
        criteria.andIn(id, Arrays.asList(ids));
        baseMapper.deleteByExample(example);
    }

    @Override
    public T findOne(Object id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(T record) {
        updateByPrimaryKey(record);
    }


    public CoreServiceImpl(Mapper<T> baseMapper, Class<T> clazz) {
        this.baseMapper = baseMapper;
        this.clazz = clazz;
    }

    @Override
    public int insert(T record) {
        return baseMapper.insert(record);
    }

    @Override
    public int insertSelective(T record) {
        return baseMapper.insert(record);
    }

    @Override
    public int delete(T record) {
        return baseMapper.delete(record);
    }

    @Override
    public int deleteByPrimaryKey(Object key) {
        return baseMapper.deleteByPrimaryKey(key);
    }

    @Override
    public int deleteByExample(Object example) {
        return baseMapper.deleteByExample(example);
    }

    @Override
    public T selectOne(T record) {
        return baseMapper.selectOne(record);
    }

    @Override
    public List<T> select(T record) {
        return baseMapper.select(record);
    }

    @Override
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    @Override
    public T selectByPrimaryKey(Object key) {
        return baseMapper.selectByPrimaryKey(key);
    }

    @Override
    public List<T> selectByExample(Object example) {
        return baseMapper.selectByExample(example);
    }

    @Override
    public int updateByPrimaryKey(T record) {
        return baseMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        return baseMapper.updateByPrimaryKeySelective(record);
    }

}
