package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.CartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse<CartVO> addCart(Integer userId, Integer productId, Integer count) {
        if(userId ==null || productId ==null || count == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        Cart originCart = cartMapper.selectByUserIdAndProductId(userId,productId);
        //需要去查看当前登陆用户之前有没有把同样的商品加入到购物车，如果没有的话就新建一条记录，有的话就修改原来的记录
        if(originCart==null){
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setChecked(Const.CheckStatus.CHECKED);
            cart.setQuantity(count);
            cart.setProductId(productId);
            cartMapper.insertSelective(cart);
        }else{
            Cart cart = new Cart();
            cart.setChecked(Const.CheckStatus.CHECKED);
            cart.setQuantity(count+originCart.getQuantity());
            cart.setId(originCart.getId());
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //此时已完成购物车记录的添加或修改，需要返回给前端该用户整个购物车信息
        return ServerResponse.successByData(getUserCartVO(userId));

    }

    @Override
    public ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count) {
        if(userId == null|| productId == null||count ==null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"参数错误");
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart!=null)
            cart.setQuantity(count);
        if(cartMapper.updateByPrimaryKeySelective(cart)>0)
            return ServerResponse.successByData(getUserCartVO(userId));
        return ServerResponse.fail("更新失败");
    }

    @Override
    public ServerResponse<CartVO> delete(Integer userId, String productIds) {
        if(userId == null || StringUtils.isBlank(productIds))
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        List<String> list = Splitter.on(",").splitToList(productIds);
        List<Integer> productIdList = list.stream().map(Integer::valueOf).collect(Collectors.toList());
        if(cartMapper.deleteByUserIdAndProductIds(userId,productIdList)>0)
            return ServerResponse.successByData(getUserCartVO(userId));
        return ServerResponse.fail("删除失败");
    }

    @Override
    public ServerResponse<CartVO> selectAll(Integer userId) {
        if(userId==null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        if(cartMapper.updateCheckStatus(userId,null,Const.CheckStatus.CHECKED)>0)
            return ServerResponse.successByData(getUserCartVO(userId));
        return ServerResponse.fail("更新失败");
    }

    @Override
    public ServerResponse<CartVO> unSelectAll(Integer userId) {
        if(userId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        if(cartMapper.updateCheckStatus(userId,null,Const.CheckStatus.UNCHECKED)>0)
            return ServerResponse.successByData(getUserCartVO(userId));
        return ServerResponse.fail("反选失败");
    }

    @Override
    public ServerResponse<CartVO> select(Integer userId, Integer productId) {
        if(userId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        if(cartMapper.updateCheckStatus(userId,productId,Const.CheckStatus.CHECKED)>0)
            return ServerResponse.successByData(getUserCartVO(userId));
        return ServerResponse.fail("单选失败");
    }

    @Override
    public ServerResponse<CartVO> unSelect(Integer userId, Integer productId) {
        if(userId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        if(cartMapper.updateCheckStatus(userId,productId,Const.CheckStatus.UNCHECKED)>0)
            return ServerResponse.successByData(getUserCartVO(userId));
        return ServerResponse.fail("单反选失败");
    }

    @Override
    public ServerResponse<CartVO> list(Integer userId) {
        if(userId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        return ServerResponse.successByData(getUserCartVO(userId));
    }

    @Override
    public ServerResponse<Integer> getTotalQuantity(Integer userId) {
        if(userId == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(),"传参错误");
        return ServerResponse.successByData(cartMapper.getTotalQuantity(userId));
    }






    private CartVO getUserCartVO(Integer userId){
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        CartVO cartVO = new CartVO();
        cartList.forEach(cartItem -> {
            CartProductVO cartProductVO = new CartProductVO();
            BeanUtils.copyProperties(cartItem,cartProductVO);
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(product!=null){
                cartProductVO.setProductName(product.getName());
                cartProductVO.setProductPrice(product.getPrice());
                cartProductVO.setProductStatus(product.getStatus());
                cartProductVO.setProductMainImage(product.getMainImage());
                cartProductVO.setProductSubtitle(product.getSubtitle());
                cartProductVO.setProductStock(product.getStock());

                //判断库存
                if(product.getStock()>=cartItem.getQuantity()){
                    cartProductVO.setLimitQuantity(Const.LimitStatus.LIMIT_NUM_SUCCESS);
                }else{
                    cartProductVO.setQuantity(product.getStock());
                    cartProductVO.setLimitQuantity(Const.LimitStatus.LIMIT_NUM_FAIL);
                    //更新数据库的数量
                    Cart cartUpdate = new Cart();
                    cartUpdate.setId(cartItem.getId());
                    cartUpdate.setQuantity(product.getStock());
                    cartMapper.updateByPrimaryKeySelective(cartUpdate);
                }
                cartProductVO.setProductTotalPrice(BigDecimalUtil.multiply(Double.valueOf(cartProductVO.getQuantity()),cartProductVO.getProductPrice().doubleValue()));

                if(cartItem.getChecked() == Const.CheckStatus.CHECKED)
                    cartVO.setCartTotalPrice(BigDecimalUtil.add(cartVO.getCartTotalPrice().doubleValue(),cartProductVO.getProductTotalPrice().doubleValue()));

                cartProductVOList.add(cartProductVO);
            }
        });
        cartVO.setCartProductVoList(cartProductVOList);
        cartVO.setImageHost(PropertiesUtil.get("ftp.server.http.prefix"));
        cartVO.setAllChecked(cartMapper.selectUncheckedCount(userId)==0);

        return cartVO;
    }
}
