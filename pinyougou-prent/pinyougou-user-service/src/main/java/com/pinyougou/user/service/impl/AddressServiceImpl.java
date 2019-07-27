package com.pinyougou.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class AddressServiceImpl extends CoreServiceImpl<TbAddress> implements AddressService {

	
	private TbAddressMapper addressMapper;

	@Autowired
	public AddressServiceImpl(TbAddressMapper addressMapper) {
		super(addressMapper, TbAddress.class);
		this.addressMapper=addressMapper;
	}

	
	

	
	@Override
    public PageInfo<TbAddress> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbAddress> all = addressMapper.selectAll();
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbAddress> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbAddress> findPage(Integer pageNo, Integer pageSize, TbAddress address) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbAddress.class);
        Example.Criteria criteria = example.createCriteria();

        if(address!=null){			
						if(StringUtils.isNotBlank(address.getUserId())){
				criteria.andLike("userId","%"+address.getUserId()+"%");
				//criteria.andUserIdLike("%"+Address.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(address.getProvinceId())){
				criteria.andLike("provinceId","%"+address.getProvinceId()+"%");
				//criteria.andProvinceIdLike("%"+Address.getProvinceId()+"%");
			}
			if(StringUtils.isNotBlank(address.getCityId())){
				criteria.andLike("cityId","%"+address.getCityId()+"%");
				//criteria.andCityIdLike("%"+Address.getCityId()+"%");
			}
			if(StringUtils.isNotBlank(address.getTownId())){
				criteria.andLike("townId","%"+address.getTownId()+"%");
				//criteria.andTownIdLike("%"+Address.getTownId()+"%");
			}
			if(StringUtils.isNotBlank(address.getMobile())){
				criteria.andLike("mobile","%"+address.getMobile()+"%");
				//criteria.andMobileLike("%"+Address.getMobile()+"%");
			}
			if(StringUtils.isNotBlank(address.getAddress())){
				criteria.andLike("Address","%"+address.getAddress()+"%");
				//criteria.andAddressLike("%"+Address.getAddress()+"%");
			}
			if(StringUtils.isNotBlank(address.getContact())){
				criteria.andLike("contact","%"+address.getContact()+"%");
				//criteria.andContactLike("%"+Address.getContact()+"%");
			}
			if(StringUtils.isNotBlank(address.getIsDefault())){
				criteria.andLike("isDefault","%"+address.getIsDefault()+"%");
				//criteria.andIsDefaultLike("%"+Address.getIsDefault()+"%");
			}
			if(StringUtils.isNotBlank(address.getNotes())){
				criteria.andLike("notes","%"+address.getNotes()+"%");
				//criteria.andNotesLike("%"+Address.getNotes()+"%");
			}
			if(StringUtils.isNotBlank(address.getAlias())){
				criteria.andLike("alias","%"+address.getAlias()+"%");
				//criteria.andAliasLike("%"+Address.getAlias()+"%");
			}
	
		}
        List<TbAddress> all = addressMapper.selectByExample(example);
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbAddress> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public List<TbAddress> findAllByUserName(String username) {
		Example example=new Example(TbAddress.class);//其中tbaddress的作用为确定要操作的表
		example.createCriteria().andEqualTo("userId",username);
		List<TbAddress> tbAddresses = addressMapper.selectByExample(example);
		return tbAddresses;
	}

}
