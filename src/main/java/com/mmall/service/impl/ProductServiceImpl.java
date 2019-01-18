package com.mmall.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.CategoryService;
import com.mmall.service.ProductService;
import com.mmall.util.DateUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Resource(name = "categoryService")
    CategoryService categoryService;

    @Override
    public ServerResponse addOrUpdateProduct(Product product) {
        if (product == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "需要传递产品信息");
        if (StringUtils.isNotBlank(product.getSubImages()))
            product.setMainImage(product.getSubImages().split(",")[0]);
        if (product.getId() != null && productMapper.updateByPrimaryKeySelective(product) > 0)
            return ServerResponse.successByMsg("更新成功");
        if (product.getId() == null && productMapper.insertSelective(product) > 0)
            return ServerResponse.successByMsg("保存成功");
        return ServerResponse.fail("更新或保存失败");
    }

    @Override
    public ServerResponse updateStatus(Integer productId, Integer status) {
        if (productId == null || status == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        if (productMapper.updateByPrimaryKeySelective(product) > 0)
            return ServerResponse.successByMsg("更新状态成功");
        return ServerResponse.fail("更新状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if (productId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.fail("商品不存在");
        return ServerResponse.successByData(getProductDetailVOFromPOJO(product));

    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        //开启功能
        PageHelper.startPage(pageNum, pageSize);

        //执行查询语句，通过aop添加分页功能，返回page对象
        List<Product> productList = productMapper.selectAllProducts();

        //通过pageinfo装载page对象的分页信息及查询结果list
        PageInfo pageInfo = new PageInfo(productList);

        //把bo转化为vo存在list中，替换掉pageinfo的bolist，分页信息保持不变
        List<ProductListVO> productListVOList = new ArrayList<>();
        productList.forEach(product -> productListVOList.add(getProductListVOFromPOJO(product)));

        pageInfo.setList(productListVOList);

        return ServerResponse.successByData(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> search(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        //模糊查询sql
        if (StringUtils.isNotBlank(productName))
            productName = "%" + productName + "%";
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectProductsByNameAndId(productName, productId);
        PageInfo pageInfo = new PageInfo(productList);
        List<ProductListVO> productListVOList = new ArrayList<>();
        productList.forEach(product -> productListVOList.add(getProductListVOFromPOJO(product)));
        pageInfo.setList(productListVOList);
        return ServerResponse.successByData(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVO> getOnSaleProductDetail(Integer productId) {
        if (productId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.fail("商品不存在");
        if(product.getStatus() != Const.ProductStatus.ON_SALE.getStatus())
            return ServerResponse.fail("商品未在售");
        return ServerResponse.successByData(getProductDetailVOFromPOJO(product));
    }

    @Override
    public ServerResponse<PageInfo> getOnSaleProductList(String productName, Integer categoryId, Integer pageNum, Integer pageSize, String sort) {
        if (StringUtils.isBlank(productName) && categoryId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");

        List<Integer> categoryIdList = null;

        if (categoryId != null) {
            if (categoryMapper.selectByPrimaryKey(categoryId) == null && StringUtils.isBlank(productName)) {
                PageHelper.startPage(pageNum, pageSize);
                PageInfo pageInfo = new PageInfo(Lists.newArrayList());
                return ServerResponse.successByData(pageInfo);
            }
            categoryIdList = categoryService.getCurrentAndDeepChildrenId(categoryId).getData();
        }
        if (StringUtils.isNotBlank(productName))
            productName = "%" + productName + "%";

        //开启分页
        PageHelper.startPage(pageNum, pageSize);
        //开启排序,注意填写规则为 数据库字段名+空格+desc/asc
        if(StringUtils.isNotBlank(sort)){
            if(Const.OrderBy.orderByPrice.contains(sort))
                PageHelper.orderBy(sort.replace("_"," "));
            else
                PageHelper.orderBy(sort.substring(0,sort.lastIndexOf("_"))+" "+sort.substring(sort.lastIndexOf("_")+1));
        }
        List<Product> productList = productMapper.selectProductsOnSale(StringUtils.isBlank(productName)?null:productName,CollectionUtils.isEmpty(categoryIdList)?null:categoryIdList);
        PageInfo pageInfo = new PageInfo(productList);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        productList.forEach(product -> productListVOList.add(getProductListVOFromPOJO(product)));
        pageInfo.setList(productListVOList);
        return ServerResponse.successByData(pageInfo);

    }

    private ProductDetailVO getProductDetailVOFromPOJO(Product product) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, productDetailVO);

        productDetailVO.setCreateTime(DateUtil.getStrFromDate(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateUtil.getStrFromDate(product.getUpdateTime()));

        productDetailVO.setImageHost(PropertiesUtil.get("ftp.server.http.prefix", "http://image.imoooc.com/"));
        productDetailVO.setParentCategoryId(categoryMapper.selectByPrimaryKey(product.getCategoryId()).getParentId());
        return productDetailVO;
    }

    private ProductListVO getProductListVOFromPOJO(Product product) {
        ProductListVO productListVO = new ProductListVO();
        BeanUtils.copyProperties(product, productListVO);

        productListVO.setImageHost(PropertiesUtil.get("ftp.server.http.prefix", "http://image.imoooc.com/"));

        return productListVO;
    }
}
