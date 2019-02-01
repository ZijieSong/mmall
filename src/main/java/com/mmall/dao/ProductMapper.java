package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectAllProducts();

    List<Product> selectProductsByNameAndId(@Param("name") String name,@Param("id")Integer id);

    List<Product> selectProductsOnSale(@Param("name") String name, @Param("categoryIdList") List<Integer> categoryIdList);

    int updateBatch(@Param("productList") List<Product> productList);
}