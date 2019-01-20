package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVO;

public interface CartService {
    ServerResponse<CartVO> addCart(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVO> delete(Integer userId, String productIds);
    ServerResponse<CartVO> selectAll(Integer userId);
    ServerResponse<CartVO> unSelectAll(Integer userId);
    ServerResponse<CartVO> select(Integer userId, Integer productId);
    ServerResponse<CartVO> unSelect(Integer userId, Integer productId);
    ServerResponse<CartVO> list(Integer userId);
    ServerResponse<Integer> getTotalQuantity(Integer userId);
}
