package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

public interface ProductService {
    ServerResponse addOrUpdateProduct(Product product);

    ServerResponse updateStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVO> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> search(String productName, Integer productId, Integer pageNum, Integer pageSize);

    ServerResponse<ProductDetailVO> getOnSaleProductDetail(Integer productId);

    ServerResponse<PageInfo> getOnSaleProductList(String productName, Integer categoryId, Integer pageNum, Integer pageSize, String sort);
}
