package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

public interface ProductService {
    ServerResponse addOrUpdateProduct(Product product);

    ServerResponse updateStatus(Integer productId, Integer status);
}
