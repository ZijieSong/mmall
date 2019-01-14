package com.mmall.common;

public enum ResponseCode {

    SUCCESS(0,"success"),
    ERROR(1,"error"),
    NEED_LOGIN(10,"need login"),
    ILLEGAL_PARAM(2,"illegal param");


    private int status;
    private String msg;

    ResponseCode(int status, String msg){
        this.msg = msg;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
