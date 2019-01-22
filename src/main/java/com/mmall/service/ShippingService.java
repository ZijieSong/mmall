package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

public interface ShippingService {
    ServerResponse addShipping(Integer userId, Shipping shipping);
    ServerResponse deleteShipping(Integer userId, Integer shippingId);
    ServerResponse updateShipping(Integer userId, Shipping shipping);
    ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId);
    ServerResponse<PageInfo> getShippingList(Integer userId, Integer pageNum, Integer pageSize);
}
