package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role {
        int ROLE_CUSTOMER = 1; //普通用户
        int ROLE_ADMIN = 0;//管理员
    }

    public interface OrderBy{
        Set<String> orderByPrice = Sets.newHashSet("price_desc","price_asc");
    }

    public enum ProductStatus{
        ON_SALE(1,"在售");

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
}
