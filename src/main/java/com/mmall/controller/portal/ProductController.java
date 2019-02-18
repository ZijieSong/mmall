package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.ProductService;
import com.mmall.vo.ProductDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Resource(name = "productService")
    private ProductService productService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(Integer productId) {
        return productService.getOnSaleProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(@RequestParam(value = "productName", required = false) String productName,
                                            @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(value = "sort", required = false) String sort) {
        return productService.getOnSaleProductList(productName, categoryId, pageNum, pageSize, sort);
    }

    @RequestMapping("/list/product/{productName}")
    @ResponseBody
    public ServerResponse<PageInfo> getListRestful(@PathVariable(value = "productName") String productName,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(value = "sort", required = false) String sort) {
        return productService.getOnSaleProductList(productName, null, pageNum, pageSize, sort);
    }
}
