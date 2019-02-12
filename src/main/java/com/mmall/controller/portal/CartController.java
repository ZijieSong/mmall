package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.HostHolder;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Cart;
import com.mmall.pojo.User;
import com.mmall.service.CartService;
import com.mmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    HostHolder hostHolder;

    @Resource(name = "cartService")
    private CartService cartService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse<CartVO> addCart(Integer productId, Integer count){
        User user = hostHolder.getUser();
        return cartService.addCart(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(Integer productId, Integer count){
        User user = hostHolder.getUser();
        return cartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVO> delete(String productIds){
        User user = hostHolder.getUser();
        return cartService.delete(user.getId(),productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(){
        User user = hostHolder.getUser();
        return cartService.selectAll(user.getId());
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(){
        User user = hostHolder.getUser();
        return cartService.unSelectAll(user.getId());
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVO> select(Integer productId){
        User user = hostHolder.getUser();
        return cartService.select(user.getId(),productId);
    }

    @RequestMapping("unSelect.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelect(Integer productId){
        User user = hostHolder.getUser();
        return cartService.unSelect(user.getId(),productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVO> list(){
        User user = hostHolder.getUser();
        return cartService.list(user.getId());
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(){
        User user = hostHolder.getUser();
        return cartService.getTotalQuantity(user.getId());
    }

}
