package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {
    //总的购物车信息

    //list是购物车中的每一条记录的集合
    private List<CartProductVO> cartProductVoList;
    private BigDecimal cartTotalPrice = new BigDecimal(0);
    private Boolean allChecked;//是否已经都勾选
    private String imageHost;

    public List<CartProductVO> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVO> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
