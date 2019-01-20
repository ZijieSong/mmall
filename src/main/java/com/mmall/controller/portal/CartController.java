package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Cart;
import com.mmall.pojo.User;
import com.mmall.service.CartService;
import com.mmall.vo.CartVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Resource(name = "cartService")
    private CartService cartService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse<CartVO> addCart(HttpSession httpSession, Integer productId, Integer count){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        return cartService.addCart(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpSession httpSession, Integer productId, Integer count){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        return cartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVO> delete(HttpSession session, String productIds){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.delete(user.getId(),productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.selectAll(user.getId());
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.unSelectAll(user.getId());
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVO> select(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.select(user.getId(),productId);
    }

    @RequestMapping("unSelect.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelect(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.unSelect(user.getId(),productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVO> list(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.list(user.getId());
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.getTotalQuantity(user.getId());
    }

}
