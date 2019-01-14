package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.CategoryService;
import com.mmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "categoryService")
    private CategoryService categoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        return categoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(String categoryName, Integer categoryId) {
        return categoryService.setCategoryName(categoryName, categoryId);
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenCategory(
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        return categoryService.getChildrenCategory(categoryId);
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        return categoryService.getCurrentAndDeepChildrenId(categoryId);
    }
}
