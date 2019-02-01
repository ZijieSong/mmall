package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.vo.OrderVO;


import java.util.Map;

public interface OrderService {
    ServerResponse getOrderQRURL(Integer userId, Long orderNo, String localPath);
    Boolean verifySign(Map<String, String> params);
    ServerResponse paySuccess(Map<String, String> params);
    ServerResponse payedStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);
    ServerResponse cancel(Integer userId, Long orderNo);
    ServerResponse getOrderProductDetail(Integer userId);
    ServerResponse detail(Integer userId, Long orderNo);
    ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> manageOrderList(Integer pageNum, Integer pageSize);
    ServerResponse<OrderVO> manageOrderDetail(Long orderNo);
    ServerResponse<PageInfo> manageSearch(Long orderNo, Integer pageNum,Integer pageSize);
    ServerResponse manageSendGoods(Long orderNo);
}
