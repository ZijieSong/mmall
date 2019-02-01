package com.mmall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.OrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVO;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;
import com.mmall.vo.ShippingVO;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    //初始化静态成员变量
    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

    }

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    PayInfoMapper payInfoMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse getOrderQRURL(Integer userId, Long orderNo, String localPath) {
        if (userId == null || orderNo == null || StringUtils.isBlank(localPath))
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "传参错误");
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order == null)
            return ServerResponse.fail("订单不存在");

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "大红书当面付扫码消费, 订单号为：" + outTradeNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "订单：" + outTradeNo + " 购买商品共：" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        orderItemList.forEach(orderItem ->
                goodsDetailList.add(GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                        BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), (double) 100).longValue(),
                        orderItem.getQuantity())));

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.get("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                //创建文件夹
                File file = new File(localPath);
                if (!file.exists()) {
                    file.setWritable(true);
                    file.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String filePath = String.format(localPath + "/qr-%s.png", response.getOutTradeNo());
                log.info("filePath:" + filePath);
                File targetFile = new File(filePath);

                //在本地生成二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                //上传到ftp服务器
                if (!FTPUtil.uploadFiles(Lists.newArrayList(targetFile), PropertiesUtil.get("ftpfile.upload.remote")))
                    return ServerResponse.fail("二维码上传失败");

                //删除本地文件
                targetFile.delete();

                Map<String, String> map = Maps.newHashMap();
                map.put("orderNo", outTradeNo);
                map.put("QRUrl", PropertiesUtil.get("ftp.server.http.prefix") + targetFile.getName());

                return ServerResponse.successByData(map);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.fail("支付宝预下单失败!!!");
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.fail("系统异常，预下单状态未知!!!");
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.fail("不支持的交易状态，交易返回异常!!!");
        }
    }

    @Override
    public Boolean verifySign(Map<String, String> params) {
        //去掉sign_type
        params.remove("sign_type");

        try {
            return AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());
        } catch (AlipayApiException e) {
            log.error("验签异常");
            return false;
        }

    }

    @Override
    public ServerResponse paySuccess(Map<String, String> params) {
        String orderNo = params.get("out_trade_no");
        Order order = orderMapper.selectByOrderNo(Long.valueOf(orderNo));

        if (order == null)
            return ServerResponse.fail("非大红书订单，忽略");

        //判断是否是支付宝的重复回调
        if (order.getStatus() >= Const.OrderStatus.PAID.getValue())
            return ServerResponse.successByMsg("回调重复订单");

        //更新订单表
        order.setPaymentTime(DateUtil.getDateFromStr(params.get("gmt_payment")));
        order.setStatus(Const.OrderStatus.PAID.getValue());
        orderMapper.updateByPrimaryKeySelective(order);

        //更新流水表
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatform.ZHIFUBAO.getValue());
        payInfo.setPlatformNumber(params.get("trade_no"));
        payInfo.setPlatformStatus(params.get("trade_status"));
        payInfoMapper.insertSelective(payInfo);

        return ServerResponse.success();

    }

    @Override
    public ServerResponse payedStatus(Integer userId, Long orderNo) {
        if (userId == null || orderNo == null)
            return ServerResponse.fail(ResponseCode.ILLEGAL_PARAM.getStatus(), "参数传递错误");
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order == null)
            return ServerResponse.fail("订单不存在");
        return order.getStatus() >= Const.OrderStatus.PAID.getValue() ? ServerResponse.success() : ServerResponse.fail();

    }

    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //1 维护order_item表
        //2 维护order表
        //3 返回orderVO

        //取出所有购物车勾选的cart集合
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);

        ServerResponse<List<OrderItem>> orderItemResponse = getOrderItemFromCart(userId, cartList);
        if (!orderItemResponse.isSuccess())
            return orderItemResponse;
        List<OrderItem> orderItemList = orderItemResponse.getData();

        Order order = getOrder(userId, shippingId, getTotalPayment(orderItemList));
        if (order == null)
            return ServerResponse.fail("插入订单失败");

        orderItemList.forEach(orderItem -> orderItem.setOrderNo(order.getOrderNo()));
        if (orderItemMapper.insertBatch(orderItemList) < 1)
            return ServerResponse.fail("插入订单明细失败");

        //减少产品库存
        ServerResponse decreaseStockResult = decreaseStock(orderItemList);
        if (!decreaseStockResult.isSuccess())
            return decreaseStockResult;

        //清空购物车
        ServerResponse updateCart = updateCart(cartList);
        if (!updateCart.isSuccess())
            return updateCart;

        //返回前端数据
        return ServerResponse.successByData(generateOrderVO(order, orderItemList));

    }

    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order == null) {
            return ServerResponse.fail("用户不存在该订单");
        }
        if (order.getStatus() != Const.OrderStatus.NO_PAY.getValue()) {
            return ServerResponse.fail("无法取消当前订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatus.CANCELED.getValue());

        if (orderMapper.updateByPrimaryKeySelective(updateOrder) > 0)
            return ServerResponse.successByMsg("取消订单成功");
        return ServerResponse.fail("取消订单失败");
    }

    @Override
    public ServerResponse getOrderProductDetail(Integer userId) {
        //选出勾选的购物车条目
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        ServerResponse serverResponse = getOrderItemFromCart(userId, cartList);
        if (!serverResponse.isSuccess())
            return serverResponse;
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        orderItemList.forEach(orderItem -> orderItemVOList.add(generateOrderItemVO(orderItem)));

        BigDecimal payment = getTotalPayment(orderItemList);

        OrderProductVO orderProductVO = new OrderProductVO();
        orderProductVO.setProductTotalPrice(payment);
        orderProductVO.setImageHost(PropertiesUtil.get("ftp.server.http.prefix"));
        orderProductVO.setOrderItemVOList(orderItemVOList);

        return ServerResponse.successByData(orderProductVO);
    }

    @Override
    public ServerResponse detail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order == null)
            return ServerResponse.fail("订单不存在");
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        return ServerResponse.successByData(generateOrderVO(order, orderItemList));
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectOrderListByUserId(userId);
        PageInfo pageInfo = new PageInfo(orderList);

        pageInfo.setList(generateOrderVOList(orderList,userId));

        return ServerResponse.successByData(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> manageOrderList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        PageInfo pageInfo = new PageInfo(orderList);

        pageInfo.setList(generateOrderVOList(orderList,null));

        return ServerResponse.successByData(pageInfo);
    }

    @Override
    public ServerResponse<OrderVO> manageOrderDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order==null)
            return ServerResponse.fail("订单不存在");
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);

        return ServerResponse.successByData(generateOrderVO(order,orderItemList));
    }

    @Override
    public ServerResponse<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order==null)
            return ServerResponse.fail("订单不存在");
        PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        pageInfo.setList(Lists.newArrayList(generateOrderVO(order,orderItemList)));
        return ServerResponse.successByData(pageInfo);
    }

    @Override
    public ServerResponse manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order==null)
            return ServerResponse.fail("订单不存在");
        if(order.getStatus()!=Const.OrderStatus.PAID.getValue())
            return ServerResponse.fail("订单状态不允许发货");
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatus.SHIPPED.getValue());
        updateOrder.setSendTime(new Date());

        if(orderMapper.updateByPrimaryKeySelective(updateOrder)>0)
            return ServerResponse.successByData("发货成功");
        return ServerResponse.fail("发货失败");
    }

    private List<OrderVO> generateOrderVOList(List<Order> orderList, Integer userId) {
        List<OrderVO> orderVOList = Lists.newArrayList();
        orderList.forEach(order -> {
            List<OrderItem> orderItemList;
            if (userId == null) {
                orderItemList= orderItemMapper.selectByOrderNo(order.getOrderNo());
            } else {
                orderItemList = orderItemMapper.selectByOrderNoAndUserId(order.getOrderNo(), userId);
            }
            orderVOList.add(generateOrderVO(order, orderItemList));
        });
        return orderVOList;
    }

    private OrderVO generateOrderVO(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        Const.PaymentType paymentType = Const.PaymentType.getEnumFromCode(order.getPaymentType());
        orderVO.setPaymentTypeDesc(paymentType == null ? null : paymentType.getDes());
        Const.OrderStatus orderStatus = Const.OrderStatus.getOrderStatusFromValue(order.getStatus());
        orderVO.setStatusDesc(orderStatus == null ? null : orderStatus.getStatus());
        orderVO.setCloseTime(DateUtil.getStrFromDate(order.getCloseTime()));
        orderVO.setCreateTime(DateUtil.getStrFromDate(order.getCreateTime()));
        orderVO.setEndTime(DateUtil.getStrFromDate(order.getEndTime()));
        orderVO.setPaymentTime(DateUtil.getStrFromDate(order.getPaymentTime()));
        orderVO.setSendTime(DateUtil.getStrFromDate(order.getSendTime()));

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        orderItemList.forEach(orderItem -> orderItemVOList.add(generateOrderItemVO(orderItem)));
        orderVO.setOrderItemVoList(orderItemVOList);

        orderVO.setImageHost(PropertiesUtil.get("ftp.server.http.prefix"));

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVO.setShippingVO(getShippingVO(shipping));
            orderVO.setReceiverName(shipping.getReceiverName());
        }

        return orderVO;


    }

    private ShippingVO getShippingVO(Shipping shipping) {
        ShippingVO shippingVO = new ShippingVO();
        BeanUtils.copyProperties(shipping, shippingVO);
        return shippingVO;
    }

    private OrderItemVO generateOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orderItem, orderItemVO);
        orderItemVO.setCreateTime(DateUtil.getStrFromDate(orderItem.getCreateTime()));

        return orderItemVO;
    }

    private ServerResponse updateCart(List<Cart> cartList) {
        List<Integer> cartIdList = Lists.newArrayList();
        cartList.forEach(cart -> cartIdList.add(cart.getId()));
        if (cartMapper.deleteBatch(cartIdList) > 0)
            return ServerResponse.successByMsg("删除购物车信息成功");
        return ServerResponse.fail("删除购物车信息失败");
    }

    private ServerResponse decreaseStock(List<OrderItem> orderItemList) {
        List<Product> productList = Lists.newArrayList();
        orderItemList.forEach(orderItem -> {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            Product updateProduct = new Product();
            updateProduct.setId(product.getId());
            updateProduct.setStock(product.getStock()-orderItem.getQuantity());
            productList.add(updateProduct);
        });
        //批量更新
        if (productMapper.updateBatch(productList) > 0)
            return ServerResponse.successByMsg("产品库存更新成功");
        return ServerResponse.fail("产品库存更新失败");
    }

    private Order getOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setShippingId(shippingId);
        order.setUserId(userId);
        order.setPayment(payment);
        order.setPaymentType(Const.PaymentType.ONLINE_PAY.getValue());
        order.setStatus(Const.OrderStatus.NO_PAY.getValue());
        order.setPostage(0);

        if (orderMapper.insertSelective(order) > 0)
            return order;
        return null;
    }

    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(100);
    }

    private BigDecimal getTotalPayment(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }


    private ServerResponse<List<OrderItem>> getOrderItemFromCart(Integer userId, List<Cart> cartList) {
        if (CollectionUtils.isEmpty(cartList))
            return ServerResponse.fail("用户未勾选任何商品");

        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());

            //校验商品状态
            if (product.getStock() < cart.getQuantity())
                return ServerResponse.fail(String.format("商品库存不足，编号为：%d", cart.getProductId()));
            if (product.getStatus() != Const.ProductStatus.ON_SALE.getStatus())
                return ServerResponse.fail(String.format("商品未上线，编号为：%d", cart.getProductId()));

            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setUserId(userId);
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(cart.getQuantity().doubleValue(), product.getPrice().doubleValue()));

            orderItemList.add(orderItem);
        }
        return ServerResponse.successByData(orderItemList);
    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (org.apache.commons.lang.StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
