package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role {
        int ROLE_CUSTOMER = 1; //普通用户
        int ROLE_ADMIN = 0;//管理员
    }

    public interface RedisKey{
        String LOGIN_TOKEN_PREFIX = "LOGIN_TOKEN:";
        String RESET_PASSWORD_PREFIX = "PASS_RESET_TOKEN:";
    }

    public interface EXPIRE_TIME{
        int LOGIN_TOKEN_EXPIRE = 60*30;
        int PASSRESET_TOKEN_EXPIRE = 60*60*12;
    }

    public interface CheckStatus {
        int CHECKED = 1;
        int UNCHECKED = 0;
    }

    public interface LimitStatus {
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface OrderBy {
        Set<String> orderByPrice = Sets.newHashSet("price_desc", "price_asc");
    }

    public enum ProductStatus {
        ON_SALE(1, "在售");

        private int status;
        private String msg;

        ProductStatus(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }
    }

    public interface AlipayCallback {
        String ALIPAY_CALLBACK_SUCCESS = "success";
        String ALIPAY_CALLBACK_FAILED = "failed";

        String ALIPAY_CALLBACK_STATUS_WAITPAY = "WAIT_BUYER_PAY";
        String ALIPAY_CALLBACK_STATUS_PAYSUCCESS = "TRADE_SUCCESS";
    }

    public enum OrderStatus {
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已付款"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSE(60, "订单关闭");

        int value;
        String status;

        OrderStatus(int value, String status) {
            this.value = value;
            this.status = status;
        }

        public int getValue() {
            return value;
        }

        public String getStatus() {
            return status;
        }

        public static OrderStatus getOrderStatusFromValue(int value){
            for(OrderStatus orderStatus : values()){
                if(orderStatus.getValue() == value)
                    return orderStatus;
            }
            return null;
        }
    }

    public enum PayPlatform {
        ZHIFUBAO(1, "支付宝"),
        WEICHAT(2, "微信");
        int value;
        String plat;

        public int getValue() {
            return value;
        }

        public String getPlat() {
            return plat;
        }

        PayPlatform(int value, String plat) {

            this.value = value;
            this.plat = plat;
        }
    }

    public enum PaymentType {
        ONLINE_PAY(1, "在线支付");

        int value;
        String des;

        public int getValue() {
            return value;
        }

        public String getDes() {
            return des;
        }


        PaymentType(int value, String des) {

            this.value = value;
            this.des = des;
        }

        public static PaymentType getEnumFromCode(int value) {
            for (PaymentType paymentType : values()) {
                if (paymentType.getValue() == value)
                    return paymentType;
            }
            return null;
        }
    }

    public interface RedisLockKey{
        String CLOSE_ORDER_LOCK = "CLOSE_ORDER_LOCK";
    }

}
