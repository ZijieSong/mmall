package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"参数错误");
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        if(categoryMapper.insertSelective(category)>0)
            return ServerResponse.success();
        return ServerResponse.fail();
    }

    @Override
    public ServerResponse setCategoryName(String categoryName, Integer categoryId) {
        if(categoryId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        if(categoryMapper.updateByPrimaryKeySelective(category)>0)
            return ServerResponse.success();
        return ServerResponse.fail();
    }

    @Override
    public ServerResponse<List<Category>> getChildrenCategory(Integer categoryId) {
        List<Category> categories = categoryMapper.selectChildrenCategory(categoryId);
        if(CollectionUtils.isEmpty(categories))
            logger.info("未找到子目录");
        return ServerResponse.successByData(categories);
    }

    @Override
    public ServerResponse<List<Integer>> getCurrentAndDeepChildrenId(Integer categoryId) {
        List<Integer> categoryList = new ArrayList<>();
        addCurrentAndChildrenCategory(new HashSet<>(), categoryId).forEach(category -> {
            categoryList.add(category.getId());
        });
        return ServerResponse.successByData(categoryList);
    }

    private Set<Category> addCurrentAndChildrenCategory(Set<Category> categorySet, Integer categoryId){
        Category current = categoryMapper.selectByPrimaryKey(categoryId);
        if(current!=null)
            categorySet.add(current);
        List<Category> categoryList = categoryMapper.selectChildrenCategory(categoryId);
        categoryList.forEach(category -> {
            addCurrentAndChildrenCategory(categorySet,category.getId());
        });
        return categorySet;
    }
}
