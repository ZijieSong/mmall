package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Resource(name = "productService")
    private ProductService productService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse addOrUpdateProduct(Product product) {
        return productService.addOrUpdateProduct(product);
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        return productService.updateStatus(productId, status);
    }



}
