package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

public interface OrderService {
    ServerResponse getOrderQRURL(Integer userId, Long orderNo, String localPath);
    Boolean verifySign(Map<String, String> params);
    ServerResponse paySuccess(Map<String, String> params);
    ServerResponse payedStatus(Integer userId, Long orderNo);
}
