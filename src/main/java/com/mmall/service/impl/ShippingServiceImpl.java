package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("shippingService")
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    ShippingMapper shippingMapper;


    @Override
    public ServerResponse addShipping(Integer userId, Shipping shipping) {
        if(userId == null || shipping == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        shipping.setUserId(userId);
        if(shippingMapper.insertSelective(shipping)>0){
            Map<String, Object> result = new HashMap<>();
            result.put("shippingId",shipping.getId());
            return ServerResponse.successByData(result);
        }
        return ServerResponse.fail("添加失败");
    }

    @Override
    public ServerResponse deleteShipping(Integer userId, Integer shippingId) {
        if (userId == null || shippingId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        if(shippingMapper.deleteShipping(userId, shippingId)>0)
            return ServerResponse.successByMsg("删除成功");
        return ServerResponse.fail("删除失败");
    }

    @Override
    public ServerResponse updateShipping(Integer userId, Shipping shipping) {
        if(userId == null || shipping == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        shipping.setUserId(userId);
        if(shippingMapper.updateShippingByUserIdAndShippingId(shipping)>0)
            return ServerResponse.successByMsg("更新成功");
        return ServerResponse.fail("更新失败");
    }

    @Override
    public ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId) {
        if(userId == null || shippingId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
        if(shipping!=null)
            return ServerResponse.successByData(shipping);
        return ServerResponse.fail("找不到相关记录");
    }

    @Override
    public ServerResponse<PageInfo> getShippingList(Integer userId, Integer pageNum, Integer pageSize) {
        if(userId == null || pageNum == null || pageSize == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectShippingAll(userId);
        PageInfo<Shipping> pageInfo = new PageInfo<>(shippingList);
        return ServerResponse.successByData(pageInfo);

    }


}
