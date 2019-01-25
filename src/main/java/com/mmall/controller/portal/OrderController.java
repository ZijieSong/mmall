package com.mmall.controller.portal;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Resource(name = "orderService")
    private OrderService orderService;

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, HttpServletRequest request, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String localPath = request.getSession().getServletContext().getRealPath("upload");
        return orderService.getOrderQRURL(user.getId(), orderNo, localPath);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public String alipayCallback(HttpServletRequest request) {
        //获取到支付宝回调的所有请求参数，由于参数本身及个数是不确定的，这里直接通过request来全部获取
        Map<String, String[]> requestParams = request.getParameterMap();
        //将参数转到map里，格式是key->value1,value2....
        Map<String, String> params = Maps.newHashMap();
        requestParams.forEach((key, value) -> {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(value).forEach(item -> sb.append(item).append(","));
            sb.deleteCharAt(sb.length() - 1);
            params.put(key, sb.toString());
        });

        logger.info("支付宝回调参数:{} ", params.toString());

        //验签
        if (!orderService.verifySign(params))
            return Const.AlipayCallback.ALIPAY_CALLBACK_FAILED;

        //判断回调时交易状态，如果是等待付款, 直接返回，如果是付款成功，去更新数据库信息
        switch (params.get("trade_status")) {
            case Const.AlipayCallback.ALIPAY_CALLBACK_STATUS_WAITPAY:
                logger.info("等待付款回调，订单号为:{}", params.get("out_trade_no"));
                return Const.AlipayCallback.ALIPAY_CALLBACK_SUCCESS;

            case Const.AlipayCallback.ALIPAY_CALLBACK_STATUS_PAYSUCCESS:
                logger.info("支付成功回调，订单号为:{}", params.get("out_trade_no"));
                if (orderService.paySuccess(params).isSuccess())
                    return Const.AlipayCallback.ALIPAY_CALLBACK_SUCCESS;
                return Const.AlipayCallback.ALIPAY_CALLBACK_FAILED;

            default:
                logger.error("未知回调:{}", params.toString());
                return Const.AlipayCallback.ALIPAY_CALLBACK_SUCCESS;
        }
    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(orderService.payedStatus(user.getId(),orderNo).isSuccess())
            return ServerResponse.successByData(true);
        return ServerResponse.successByData(false);
    }
}
