package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse setCategoryName(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildrenCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCurrentAndDeepChildrenId(Integer categoryId);
}
