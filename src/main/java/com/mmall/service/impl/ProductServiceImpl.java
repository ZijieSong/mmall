package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse addOrUpdateProduct(Product product) {
        if (product == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "需要传递产品信息");
        if(StringUtils.isNotBlank(product.getSubImages()))
            product.setMainImage(product.getSubImages().split(",")[0]);
        if (product.getId() != null && productMapper.updateByPrimaryKeySelective(product)>0)
            return ServerResponse.successByMsg("更新成功");
        if(product.getId() == null && productMapper.insertSelective(product)>0)
            return ServerResponse.successByMsg("保存成功");
        return ServerResponse.fail("更新或保存失败");
    }

    @Override
    public ServerResponse updateStatus(Integer productId, Integer status) {
        if(productId==null || status ==null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        if(productMapper.updateByPrimaryKeySelective(product)>0)
            return ServerResponse.successByMsg("更新状态成功");
        return ServerResponse.fail("更新状态失败");
    }
}
